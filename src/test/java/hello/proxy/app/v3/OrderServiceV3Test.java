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
        assertTimeout(ofMillis(1200),
                () -> service.orderItem("itemId"));
        assertThat(getOrderedLogs().get(0)).contains("OrderServiceV3.orderItem()");
        assertThat(getOrderedLogs().get(1)).contains("|-->OrderRepositoryV3.save()");
        assertThat(getOrderedLogs().get(2)).contains("|<--OrderRepositoryV3.save() time=");
        assertThat(getOrderedLogs().get(3)).contains("OrderServiceV3.orderItem() time=");
    }

    @Test
    @DisplayName("상품 주문을 실패한다.")
    void orderItemFailTest() {
        assertThatThrownBy(() -> service.orderItem("ex"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(getOrderedLogs().get(0)).contains("OrderServiceV3.orderItem()");
        assertThat(getOrderedLogs().get(1)).contains("|-->OrderRepositoryV3.save()");
        assertThat(getOrderedLogs().get(2)).contains("|<X-OrderRepositoryV3.save() time=");
        assertThat(getOrderedLogs().get(3)).contains("OrderServiceV3.orderItem() time=");
    }
}
