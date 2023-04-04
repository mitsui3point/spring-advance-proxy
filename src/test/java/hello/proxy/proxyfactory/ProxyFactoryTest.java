package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import hello.proxy.log.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ProxyFactoryTest extends LogAppenders {
    /**
     * new ProxyFactory(target) :
     *  프록시 팩토리를 생성할 때, 생성자에 프록시의 호출 대상을 함께 넘겨준다.
     *  프록시 팩토리는 이 인스턴스 정보를 기반으로 프록시를 만들어낸다.
     *  만약 이 인스턴스에 인터페이스가 있다면 JDK 동적 프록시를 기본으로 사용하고 인터페이스가 없고 구체 클래스만 있다면 CGLIB를 통해서 동적 프록시를 생성한다.
     *  여기서는 target 이 new ServiceImpl() 의 인스턴스이기 때문에 ServiceInterface 인터페이스가 있다.
     *  따라서 이 인터페이스를 기반으로 JDK 동적 프록시를 생성한다.
     * proxyFactory.addAdvice(new TimeAdvice()) :
     *  프록시 팩토리를 통해서 만든 프록시가 사용할 부가 기능 로직을 설정한다.
     *  JDK 동적 프록시가 제공하는 InvocationHandler 와 CGLIB가 제공하는 MethodInterceptor 의 개념과 유사하다.
     *  이렇게 프록시가 제공하는 부가 기능 로직을 어드바이스 ( Advice )라 한다.
     *  번역하면 조언을 해준다고 생각하면 된다.
     * proxyFactory.getProxy() :
     *  프록시 객체를 생성하고 그 결과를 받는다.
     */
    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    void interfaceProxy() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
        proxy.save();
        assertThat(getContainsLog("targetClass=class hello.proxy.common.service.ServiceImpl")).isPresent();
        assertThat(getContainsLog("proxyClass=class com.sun.proxy.$Proxy")).isPresent();
        assertThat(getContainsLog("TimeProxy 실행")).isPresent();
        assertThat(getContainsLog("save 호출")).isPresent();
        assertThat(getContainsLog("TimeProxy 종료 resultTime=")).isPresent();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();//프록시 팩토리를 통해서 프록시가 생성되면 JDK 동적 프록시나, CGLIB 모두 참이다.
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();//프록시 팩토리를 통해서 프록시가 생성되고, JDK 동적 프록시인 경우 참.
        assertThat(AopUtils.isCglibProxy(proxy)).isFalse();//프록시 팩토리를 통해서 프록시가 생성되고, CGLIB 동적 프록시인 경우 경우 참.
    }

    @Test
    @DisplayName("구체클래스가 있으면 JDK 동적 프록시 사용")
    void concreteProxy() {
        ConcreteService target = new ConcreteService();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());
        ConcreteService proxy = (ConcreteService) proxyFactory.getProxy();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
        proxy.call();
        assertThat(getContainsLog("targetClass=class hello.proxy.common.service.ConcreteService")).isPresent();
        assertThat(getContainsLog("proxyClass=class hello.proxy.common.service.ConcreteService$$EnhancerBySpringCGLIB$$")).isPresent();
        assertThat(getContainsLog("TimeProxy 실행")).isPresent();
        assertThat(getContainsLog("ConcreteService 실행")).isPresent();
        assertThat(getContainsLog("TimeProxy 종료 resultTime=")).isPresent();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }

    /**
     * 스프링 부트는 AOP를 적용할 때 기본적으로 proxyTargetClass=true 로 설정해서 사용한다.
     * 따라서 인터페이스가 있어도 항상 CGLIB를 사용해서 구체 클래스를 기반으로 프록시를 생성한다.
     * 자세한 이유는 강의 뒷 부분에서 설명한다.
     */
    @Test
    @DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB 를 사용하고, 클래스 기반 프록시 사용")
    void proxyTargetClass() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(true);//프록시 팩토리는 proxyTargetClass 라는 옵션을 제공하는데, 이 옵션에 true 값을 넣으면 인터페이스가 있어도 강제로 CGLIB를 사용한다. 그리고 인터페이스가 아닌 클래스 기반의 프록시를 만들어준다.
        proxyFactory.addAdvice(new TimeAdvice());
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
        proxy.save();
        assertThat(getContainsLog("targetClass=class hello.proxy.common.service.ServiceImpl")).isPresent();
        assertThat(getContainsLog("proxyClass=class hello.proxy.common.service.ServiceImpl$$EnhancerBySpringCGLIB$$")).isPresent();
        assertThat(getContainsLog("TimeProxy 실행")).isPresent();
        assertThat(getContainsLog("save 호출")).isPresent();
        assertThat(getContainsLog("TimeProxy 종료 resultTime=")).isPresent();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }
}
