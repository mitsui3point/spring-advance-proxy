package hello.proxy.pureproxy.proxy;

import hello.proxy.log.LogAppenders;
import hello.proxy.pureproxy.proxy.code.CacheProxy;
import hello.proxy.pureproxy.proxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

/**
 * 프록시 패턴 vs 데코레이터 패턴
 * - 여기까지 진행하면 몇가지 의문이 들 것이다.
 * - Decorator 라는 추상 클래스를 만들어야 데코레이터 패턴일까?
 * - 프록시 패턴과 데코레이터 패턴은 그 모양이 거의 비슷한 것 같은데?
 *
 * 의도(intent)
 * - 사실 프록시 패턴과 데코레이터 패턴은 그 모양이 거의 같고, 상황에 따라 정말 똑같을 때도 있다.
 * - 그러면, 둘을 어떻게 구분하는 것일까?
 * - 디자인 패턴에서 중요한 것은 해당 패턴의 겉모양이 아니라 그 패턴을 만든 의도가 더 중요하다.
 * - 따라서, 의도에 따라 패턴을 구분한다.
 *
 * 프록시 패턴
 * - 프록시 패턴의 의도: 다른 개체에 대한 접근을 제어하기 위해 대리자를 제공
 */
public class ProxyTest extends LogAppenders {
    @Test
    @DisplayName("프록시 적용 전, 클라이언트가 RealSubject 의 operation 메서드를 1초씩 3번 호출한다")
    void noProxyTest() {
        //given
        RealSubject realSubject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(realSubject);
        //then
        assertTimeoutPreemptively(Duration.ofMillis(3600),
                //when
                () -> {
                    client.execute();
                    client.execute();
                    client.execute();
                });
        //then
        List<String> actualLogs = getOrderedLogs();
        assertThat(actualLogs).containsExactlyInAnyOrder(
                "[INFO] 실제 객체 호출",
                "[INFO] 실제 객체 호출",
                "[INFO] 실제 객체 호출"
        );
    }

    @Test
    @DisplayName("프록시 적용, 클라이언트가 RealSubject 의 operation 메서드를 1초씩 1번만 호출하고, 뒤에 2번 호출은 cacheValue 를 활용한다.")
    void cacheProxyTest() {
        //given
        RealSubject realSubject = new RealSubject();
        CacheProxy cache = new CacheProxy(realSubject);
        ProxyPatternClient client = new ProxyPatternClient(cache);
        //then
        assertTimeoutPreemptively(Duration.ofMillis(1200),
                //when
                () -> {
                    client.execute();
                    client.execute();
                    client.execute();
                });

        //then
        List<String> actualLogs = getOrderedLogs();
        assertThat(actualLogs).containsExactlyInAnyOrder(
                "[INFO] 프록시 객체 호출",
                "[INFO] 실제 객체 호출",
                "[INFO] 프록시 객체 호출",
                "[INFO] 프록시 객체 호출"
        );
    }
}
