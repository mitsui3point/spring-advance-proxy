package hello.proxy.app.v2;

import hello.proxy.app.v1.OrderRepositoryV1;
import hello.proxy.log.LogAppender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTimeout;

public class OrderRepositoryV2Test extends LogAppender {

    @Autowired
    private OrderRepositoryV2 repository;

    @Test
    @DisplayName("주문한 상품을 1초 후에 저장한다.")
    void saveTest() {
        assertTimeout(ofMillis(1300),
                () -> repository.save("itemId"));
    }

    @Test
    @DisplayName("주문한 상품 저장에 실패한다.")
    void saveFailTest() {
        assertThatThrownBy(() -> repository.save("ex"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}