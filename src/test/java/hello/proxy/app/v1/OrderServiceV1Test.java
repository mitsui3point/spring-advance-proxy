package hello.proxy.app.v1;

import hello.proxy.log.LogAppender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static java.time.Duration.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@SpringBootTest
public class OrderServiceV1Test extends LogAppender {

    @Autowired
    OrderServiceV1 service;

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
