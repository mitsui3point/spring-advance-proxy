package hello.proxy.app.v3;

import hello.proxy.log.LogAppenders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@SpringBootTest
public class OrderServiceV3Test extends LogAppenders {

    @Autowired
    OrderServiceV3 service;

    @Test
    @DisplayName("상품을 1초 후에 주문한다.")
    void orderItemTest() {
        ElapsedTimeChecker actual = new ElapsedTimeChecker(() -> service.orderItem("itemId"));
        assertThat(actual.elapsedTime()).isBetween(900L, 2000L);
        assertOrderItemLog(3, false);
    }

    @Test
    @DisplayName("상품 주문을 실패한다.")
    void orderItemFailTest() {
        assertThatThrownBy(() -> service.orderItem("ex"))
                .isInstanceOf(IllegalArgumentException.class);
        assertOrderItemLog(3, true);
    }
}
