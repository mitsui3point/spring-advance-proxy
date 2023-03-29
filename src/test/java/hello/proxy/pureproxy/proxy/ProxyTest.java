package hello.proxy.pureproxy.proxy;

import ch.qos.logback.classic.spi.ILoggingEvent;
import hello.proxy.log.LogAppender;
import hello.proxy.pureproxy.proxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProxyTest extends LogAppender {
    @Test
    @DisplayName("프록시 적용 전, 클라이언트가 subject 의 operation 메서드를 1초 후 호출한다")
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
        Optional<ILoggingEvent> actualLogs = getContainsLog("실제 객체 호출");
        assertThat(actualLogs).isPresent();
    }
}
