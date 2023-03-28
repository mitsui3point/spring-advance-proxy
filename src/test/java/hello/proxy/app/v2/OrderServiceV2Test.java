package hello.proxy.app.v2;

import hello.proxy.app.v1.OrderServiceV1;
import hello.proxy.log.LogAppender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTimeout;

public class OrderServiceV2Test extends LogAppender {

    @Autowired
    OrderServiceV2 service;

    @Test
    @DisplayName("상품을 1초 후에 주문한다.")
    void orderItemTest() {
        assertTimeout(ofMillis(1200),
                () -> service.orderItem("itemId"));
    }

    @Test
    @DisplayName("상품 주문을 실패한다.")
    void orderItemFailTest() {
        assertThatThrownBy(() -> service.orderItem("ex"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
