package hello.proxy.pureproxy.decorator;


import hello.proxy.log.LogAppenders;
import hello.proxy.pureproxy.decorator.code.Component;
import hello.proxy.pureproxy.decorator.code.DecoratorPatternClient;
import hello.proxy.pureproxy.decorator.code.RealComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@SpringBootTest
public class DecoratorTest extends LogAppenders {
    @Test
    @DisplayName("데코레이터 적용 전, 클라이언트가 RealComponent 의 operation 메서드를 1초씩 3번 호출한다")
    void noDecorator() {
        //given
        Component realComponent = new RealComponent();
        DecoratorPatternClient client = new DecoratorPatternClient(realComponent);
        //when
        client.execute();
        //then
        assertThat(getOrderedLogs()).containsExactlyInAnyOrder(
                "[INFO] RealComponent 실행",
                "[INFO] result=data"
                );
    }
}
