package hello.proxy.app.v1;

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
public class OrderRepositoryV1Test extends LogAppenders {

    @Autowired
    private OrderRepositoryV1 repository;

    @Test
    @DisplayName("주문한 상품을 1초 후에 저장한다.")
    void saveTest() {
        //when
        ElapsedTimeChecker actual = new ElapsedTimeChecker(() ->
                repository.save("itemId"));
        //then
        assertThat(actual.elapsedTime()).isBetween(900L, 2000L);
        assertThat(getContainsLog("OrderRepositoryV1.save()"));
        assertThat(getContainsLog("OrderRepositoryV1.save() time="));
    }

    @Test
    @DisplayName("주문한 상품 저장에 실패한다.")
    void saveFailTest() {
        assertThatThrownBy(() -> repository.save("ex"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(getContainsLog("OrderRepositoryV1.save()"));
        assertThat(getContainsLog("OrderRepositoryV1.save() time="));
        assertThat(getContainsLog("ms ex="));
    }

    @RequiredArgsConstructor
    static class ElapsedTimeChecker {
        private final OrderServiceV1Test.Callback callback;
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
