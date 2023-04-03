package hello.proxy.app.v2;

import hello.proxy.app.v1.OrderServiceV1Test;
import hello.proxy.log.LogAppenders;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@SpringBootTest
public class OrderServiceV2Test extends LogAppenders {

    @Autowired
    OrderServiceV2 service;

    @Test
    @DisplayName("상품을 1초 후에 주문한다.")
    void orderItemTest() {
        //when
        ElapsedTimeChecker actual = new ElapsedTimeChecker(() ->
                service.orderItem("itemId"));
        //then
        assertThat(actual.elapsedTime()).isBetween(900L, 2000L);
        assertThat(getContainsLog("OrderServiceV2.orderItem()")).isPresent();
        assertThat(getContainsLog("|-->OrderRepositoryV2.save()")).isPresent();
        assertThat(getContainsLog("|<--OrderRepositoryV2.save() time=")).isPresent();
        assertThat(getContainsLog("OrderServiceV2.orderItem() time=")).isPresent();
    }

    @Test
    @DisplayName("상품 주문을 실패한다.")
    void orderItemFailTest() {
        assertThatThrownBy(() -> service.orderItem("ex"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(getContainsLog("OrderServiceV2.orderItem()")).isPresent();
        assertThat(getContainsLog("|-->OrderRepositoryV2.save()")).isPresent();
        assertThat(getContainsLog("|<X-OrderRepositoryV2.save() time=")).isPresent();
        assertThat(getContainsLog("OrderServiceV2.orderItem() time=")).isPresent();
    }

    @RequiredArgsConstructor
    static class ElapsedTimeChecker {
        private final Callback callback;
        long elapsedTime() {
            long startTime = System.currentTimeMillis();
            callback.call();
            return System.currentTimeMillis() - startTime;
        }
    }

    static interface Callback {
        void call();
    }
}
