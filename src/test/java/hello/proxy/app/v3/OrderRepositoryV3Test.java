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
public class OrderRepositoryV3Test extends LogAppenders {

    @Autowired
    private OrderRepositoryV3 repository;

    @Test
    @DisplayName("주문한 상품을 1초 후에 저장한다.")
    void saveTest() {
        assertTimeout(ofMillis(1300),
                () -> repository.save("itemId"));
        assertThat(getOrderedLogs().get(0)).contains("OrderRepositoryV3.save()");
        assertThat(getOrderedLogs().get(1)).contains("OrderRepositoryV3.save() time=");
    }

    @Test
    @DisplayName("주문한 상품 저장에 실패한다.")
    void saveFailTest() {
        assertThatThrownBy(() -> repository.save("ex"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(getOrderedLogs().get(0)).contains("OrderRepositoryV3.save()");
        assertThat(getOrderedLogs().get(1)).contains("OrderRepositoryV3.save() time=");
    }
}
