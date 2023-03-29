package hello.proxy.pureproxy.proxy;

import hello.proxy.log.LogAppenders;
import hello.proxy.pureproxy.proxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.proxy.code.CacheProxy;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

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
