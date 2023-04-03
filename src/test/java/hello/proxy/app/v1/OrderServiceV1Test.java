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
        ElapsedTimeChecker actual = new ElapsedTimeChecker(() ->
                service.orderItem("itemId"));
        //then
        assertThat(actual.elapsedTime()).isBetween(900L, 2000L);
        assertThat(getContainsLog("OrderServiceV1.orderItem()")).isPresent();
        assertThat(getContainsLog("|-->OrderRepositoryV1.save()")).isPresent();
        assertThat(getContainsLog("|<--OrderRepositoryV1.save() time=")).isPresent();
        assertThat(getContainsLog("OrderServiceV1.orderItem() time=")).isPresent();
    }

    @Test
    @DisplayName("상품 주문을 실패한다.")
    void orderItemFailTest() {
        assertThatThrownBy(() -> service.orderItem("ex"));
                //.isInstanceOf(IllegalArgumentException.class);
        assertThat(getContainsLog("OrderServiceV1.orderItem()")).isPresent();
        assertThat(getContainsLog("|-->OrderRepositoryV1.save()")).isPresent();
        assertThat(getContainsLog("|<X-OrderRepositoryV1.save() time=")).isPresent();
        assertThat(getContainsLog("OrderServiceV1.orderItem() time=")).isPresent();
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
