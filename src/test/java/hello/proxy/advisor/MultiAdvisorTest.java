package hello.proxy.advisor;

import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import hello.proxy.log.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiAdvisorTest extends LogAppenders {
    /**
     * 여러 프록시의 문제
     * 이 방법이 잘못된 것은 아니지만, 프록시를 2번 생성해야 한다는 문제가 있다. 만약 적용해야 하는
     * 어드바이저가 10개라면 10개의 프록시를 생성해야한다.
     */
    @Test
    @DisplayName("여러 프록시 여러 어드바이저")
    void multiProxyMultiAdvisorTest() {
        //client -> proxy2(advisor2) -> proxy1(advisor1) -> target
        //프록시1 생성
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        proxyFactory1.addAdvisor(advisor1);
        ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();
        //프록시2 생성
        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        proxyFactory2.addAdvisor(advisor2);
        ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();

        proxy1.save();
        proxy2.save();

        assertThat(getOrderedLogs().get(0)).contains("advice1 호출");
        assertThat(getOrderedLogs().get(1)).contains("save 호출");

        assertThat(getOrderedLogs().get(2)).contains("advice2 호출");
        assertThat(getOrderedLogs().get(3)).contains("advice1 호출");
        assertThat(getOrderedLogs().get(4)).contains("save 호출");
    }

    /**
     * 하나의 프록시, 여러 어드바이저
     *  스프링은 이 문제를 해결하기 위해 하나의 프록시에 여러 어드바이저를 적용할 수 있게 만들어두었다.
     *
     * 중요
     *  스프링의 AOP를 처음 공부하거나 사용하면, AOP 적용 수 만큼 프록시가 생성된다고 착각하게 된다.
     *  실제 많은 실무 개발자들도 이렇게 생각하는 것을 보았다.
     *  스프링은 AOP를 적용할 때, 최적화를 진행해서 지금처럼 프록시는 하나만 만들고, 하나의 프록시에 여러 어드바이저를 적용한다.
     *  정리하면 하나의 target 에 여러 AOP가 동시에 적용되어도, 스프링의 AOP는 target 마다 하나의 프록시만 생성한다.
     */
    @Test
    @DisplayName("하나의 프록시 여러 어드바이저")
    void singleProxyMultiAdvisorTest() {
        //client -> proxy(advisor2, advisor1) -> target
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        //프록시1 생성
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvisor(advisor2);//등록순서대로 advisor 가 호출된다.
        proxyFactory.addAdvisor(advisor1);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        assertThat(getOrderedLogs().get(0)).contains("advice2 호출");
        assertThat(getOrderedLogs().get(1)).contains("advice1 호출");
        assertThat(getOrderedLogs().get(2)).contains("save 호출");
    }

    @Slf4j
    static class Advice1 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice1 호출");
            return invocation.proceed();
        }
    }

    @Slf4j
    static class Advice2 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice2 호출");
            return invocation.proceed();
        }
    }
}
