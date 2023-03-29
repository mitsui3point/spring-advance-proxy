package hello.proxy.pureproxy.decorator;


import hello.proxy.log.LogAppenders;
import hello.proxy.pureproxy.decorator.code.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

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
 * 데코레이터 패턴
 * - 데코레이터 패턴의 의도: 객체에 추가 책임(기능)을 동적으로 추가하고, 기능 확장을 위한 유연한 대안 제공
 */
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
