package hello.proxy.app.v1;

import hello.proxy.log.LogAppenders;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class OrderServiceV1Test extends LogAppenders {

    @Autowired
    OrderServiceV1 service;

    @Test
    @DisplayName("상품을 1초 후에 주문한다.")
    void orderItemTest() {
        //when
        ElapsedTimeChecker actual = new ElapsedTimeChecker(() -> service.orderItem("itemId"));
        //then
        assertThat(actual.elapsedTime()).isBetween(900L, 2000L);

        assertOrderItemLog(1, false);
    }

    @Test
    @DisplayName("상품 주문을 실패한다.")
    void orderItemFailTest() {
        assertThatThrownBy(() -> service.orderItem("ex"));
                //.isInstanceOf(IllegalArgumentException.class);
        assertOrderItemLog(1, true);
    }
}
