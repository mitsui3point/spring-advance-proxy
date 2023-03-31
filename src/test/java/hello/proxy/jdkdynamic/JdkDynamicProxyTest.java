package hello.proxy.jdkdynamic;

import hello.proxy.jdkdynamic.code.*;
import hello.proxy.log.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * jdk 동적 프록시
 *
 * 실행 순서
 * 1. 클라이언트는 JDK 동적 프록시의 call() 을 실행한다.
 * 2. JDK 동적 프록시는 {@link InvocationHandler#invoke(Object, Method, Object[])} 를 호출한다.
 *    {@link TimeInvocationHandler} 가 구현체로 있으므로 {@link TimeInvocationHandler#invoke(Object, Method, Object[])} 가 호출된다.
 * 3. {@link TimeInvocationHandler} 가 내부 로직을 수행하고, method.invoke(target, args) 를 호출해서 target 인 실제 객체 {@link AImpl} 를 호출한다.
 * 4. {@link AImpl} 인스턴스의 call() 이 실행된다.
 * 5. {@link AImpl} 인스턴스의 call() 의 실행이 끝나면 {@link TimeInvocationHandler} 로 응답이 돌아온다. 시간 로그를 출력하고 결과를 반환한다.
 *
 * 정리
 * - {@link AImpl}, {@link BImpl} 각각 프록시를 만들지 않았다.
 *   프록시는 JDK 동적 프록시를 사용해서 동적으로 만들고,
 *   {@link TimeInvocationHandler} 는 공통으로 사용했다.
 * - JDK 동적 프록시 기술 덕분에 적용 대상 만큼 프록시 객체를 만들지 않아도 된다.
 *   그리고 같은 부가 기능 로직을 한번만 개발해서 공통으로 적용할 수 있다.
 *   만약 적용 대상이 100개여도 동적 프록시를 통해서 생성하고,
 *   각각 필요한 InvocationHandler 만 만들어서 넣어주면 된다.
 * - 결과적으로 프록시 클래스를 수 없이 만들어야 하는 문제도 해결하고,
 *   부가 기능 로직도 하나의 클래스에 모아서 단일 책임 원칙(SRP)도 지킬 수 있게 되었다.
 */
@Slf4j
public class JdkDynamicProxyTest extends LogAppenders {
    @Test
    void dynamicA() {
        AInterface target = new AImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        //jdk java 언어에서 제공해주는 프록시 생성기술
        AInterface proxy = (AInterface) Proxy.newProxyInstance(
                AInterface.class.getClassLoader(),//프록시가 어디 클래스로더에 생성될지
                new Class[]{AInterface.class},//어떤 인터페이스를 기반으로 프록시를 만들것이다.
                handler//java.lang.InvocationHandler interface 구현체
        );

        String result = proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        assertThat(getContainsLog("TimeProxy 실행")).isPresent();
        assertThat(getContainsLog("TimeProxy 종료 resultTime=")).isPresent();
        assertThat(getContainsLog("call() A")).isPresent();
        assertThat(getContainsLog("targetClass=class hello.proxy.jdkdynamic.code.AImpl")).isPresent();
        assertThat(getContainsLog("proxyClass=class com.sun.proxy.$Proxy")).isPresent();
        assertThat(result).isEqualTo("a");
    }

    @Test
    void dynamicB() {
        BInterface target = new BImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        //jdk java 언어에서 제공해주는 프록시 생성기술
        BInterface proxy = (BInterface) Proxy.newProxyInstance(
                BInterface.class.getClassLoader(),
                new Class[]{BInterface.class},
                handler
        );

        String result = proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        assertThat(getContainsLog("TimeProxy 실행")).isPresent();
        assertThat(getContainsLog("call() B")).isPresent();
        assertThat(getContainsLog("TimeProxy 종료 resultTime=")).isPresent();
        assertThat(getContainsLog("targetClass=class hello.proxy.jdkdynamic.code.BImpl")).isPresent();
        assertThat(getContainsLog("proxyClass=class com.sun.proxy.$Proxy")).isPresent();
        assertThat(result).isEqualTo("b");
    }
}
