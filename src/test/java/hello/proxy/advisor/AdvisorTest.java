package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import hello.proxy.log.LogAppenders;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 포인트컷( Pointcut ):
 *  어디에 부가 기능을 적용할지, 어디에 부가 기능을 적용하지 않을지 판단하는 필터링 로직이다.
 *  주로 클래스와 메서드 이름으로 필터링 한다.
 *  이름 그대로 어떤 포인트(Point)에 기능을 적용할지 하지 않을지 잘라서(cut) 구분하는 것이다.
 *
 * 어드바이스( Advice ):
 *  이전에 본 것 처럼 프록시가 호출하는 부가 기능이다.
 *  단순하게 프록시 로직이라 생각하면 된다.
 *
 * 어드바이저( Advisor ):
 *  단순하게 하나의 포인트컷과 하나의 어드바이스를 가지고 있는 것이다.
 *  쉽게 이야기해서 포인트컷1 + 어드바이스1이다.
 *
 * 정리하면 부가 기능 로직을 적용해야 하는데, 포인트컷으로 어디에? 적용할지 선택하고, 어드바이스로 어떤 로직을 적용할지 선택하는 것이다.
 * 그리고 어디에? 어떤 로직?을 모두 알고 있는 것이 어드바이저이다.
 *
 * 쉽게 기억하기
 *  조언( Advice )을 어디( Pointcut )에 할 것인가?
 *  조언자( Advisor )는 어디( Pointcut )에 조언( Advice )을 해야할지 알고 있다.
 *
 * 역할과 책임
 *  이렇게 구분한 것은 역할과 책임을 명확하게 분리한 것이다.
 *  포인트컷은 대상 여부를 확인하는 필터 역할만 담당한다.
 *  어드바이스는 깔끔하게 부가 기능 로직만 담당한다.
 *  둘을 합치면 어드바이저가 된다.
 *  스프링의 어드바이저는 하나의 포인트컷 + 하나의 어드바이스로 구성된다.
 */
public class AdvisorTest extends LogAppenders {
    @Test
    void advisorTest1() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        /* new DefaultPointcutAdvisor()
        Advisor 인터페이스의 가장 일반적인 구현체이다.
        생성자를 통해 하나의 포인트컷과 하나의 어드바이스를 넣어주면 된다.
        어드바이저는 하나의 포인트컷과 하나의 어드바이스로 구성된다.
          Pointcut.TRUE
        항상 true 를 반환하는 포인트컷이다.
        */
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
        /*.addAdvisor(advisor)
        프록시 팩토리에 적용할 어드바이저를 지정한다. 어드바이저는 내부에 포인트컷과 어드바이스를 모두 가지고 있다.
        따라서 어디에 어떤 부가 기능을 적용해야 할 지 어드바이스 하나로 알수있다.
        프록시 팩토리를 사용할 때 어드바이저는 필수이다.*/
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();

        assertThat(getContainsLog("TimeProxy 실행")).isPresent();
        assertThat(getContainsLog("save 호출")).isPresent();
        assertThat(getContainsLog("TimeProxy 종료 resultTime=")).isPresent();
        assertThat(getContainsLog("TimeProxy 실행")).isPresent();
        assertThat(getContainsLog("find 호출")).isPresent();
        assertThat(getContainsLog("TimeProxy 종료 resultTime=")).isPresent();
    }
}
