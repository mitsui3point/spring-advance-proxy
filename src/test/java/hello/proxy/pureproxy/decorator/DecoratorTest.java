package hello.proxy.pureproxy.decorator;


import hello.proxy.log.LogAppenders;
import hello.proxy.pureproxy.decorator.code.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DecoratorTest extends LogAppenders {
    @Test
    @DisplayName("데코레이터 적용 전, 클라이언트가 RealComponent 의 operation 메서드를 호출한다")
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

    @Test
    @DisplayName("데코레이터 적용, 클라이언트가 RealComponent 의 operation 메서드를 호출한다")
    void decorator1() {
        //given
        Component realComponent = new RealComponent();
        Component messageDecorator = new MessageDecorator(realComponent);
        DecoratorPatternClient client = new DecoratorPatternClient(messageDecorator);
        //when
        client.execute();
        //then
        assertThat(getOrderedLogs()).containsExactlyInAnyOrder(
                "[INFO] MessageDecorator 실행",
                "[INFO] RealComponent 실행",
                "[INFO] MessageDecorator 꾸미기 적용 전=data, 적용 후=*****data*****",
                "[INFO] result=*****data*****"
                );
    }

    @Test
    @DisplayName("데코레이터 추가 적용, 클라이언트가 RealComponent 의 operation 메서드를 호출한다")
    void decorator2() {
        //given
        Component realComponent = new RealComponent();
        Component messageDecorator = new MessageDecorator(realComponent);
        Component timeDecorator = new TimeDecorator(messageDecorator);
        DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);
        //when
        client.execute();
        //then
        assertThat(getContainsLog("TimeDecorator 실행" )).isPresent();
        assertThat(getContainsLog("MessageDecorator 실행" )).isPresent();
        assertThat(getContainsLog("RealComponent 실행")).isPresent();
        assertThat(getContainsLog("MessageDecorator 꾸미기 적용 전=data, 적용 후=*****data*****")).isPresent();
        assertThat(getContainsLog("TimeDecorator 종료 resultTime=")).isPresent();
        assertThat(getContainsLog("result=*****data*****")).isPresent();
    }
}
