package hello.proxy.cglib;

import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.log.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 클래스 기반 프록시는 상속을 사용하기 때문에 몇가지 제약이 있다.
 * 부모 클래스의 생성자를 체크해야 한다. CGLIB는 자식 클래스를 동적으로 생성하기 때문에 기본 생성자가 필요하다.
 *      클래스에 final 키워드가 붙으면 상속이 불가능하다. CGLIB에서는 예외가 발생한다.
 *      메서드에 final 키워드가 붙으면 해당 메서드를 오버라이딩 할 수 없다.
 * CGLIB에서는 프록시 로직이 동작하지 않는다.
 */
@Slf4j
public class CglibTest extends LogAppenders {
    @Test
    @DisplayName("CGLIB가 동적으로 내부에 구체 클래스를 상속받아서 클래스를 만든다.")
    void cglib() {
        ConcreteService target = new ConcreteService();

        Enhancer enhancer = new Enhancer();//CGLIB는 Enhancer 를 사용해서 프록시를 생성한다.
        enhancer.setSuperclass(ConcreteService.class);//CGLIB는 구체 클래스를 상속 받아서 프록시를 생성할 수 있다. 어떤 구체 클래스를 상속 받을지 지정한다.
        enhancer.setCallback(new TimeMethodInterceptor(target));//프록시에 적용할 실행 로직을 할당한다.

        ConcreteService proxy = (ConcreteService) enhancer.create();//프록시를 생성한다. 앞서 설정한 enhancer.setSuperclass(ConcreteService.class) 에서 지정한 클래스를 상속 받아서 프록시가 만들어진다.
        proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());//대상클래스$$EnhancerByCGLIB$$임의코드

        assertThat(getContainsLog("TimeProxy 실행")).isPresent();
        assertThat(getContainsLog("ConcreteService 실행")).isPresent();
        assertThat(getContainsLog("TimeProxy 종료 resultTime=")).isPresent();
        assertThat(getContainsLog("targetClass=class hello.proxy.common.service.ConcreteService")).isPresent();
        assertThat(getContainsLog("proxyClass=class hello.proxy.common.service.ConcreteService$$EnhancerByCGLIB$$")).isPresent();
    }
}
