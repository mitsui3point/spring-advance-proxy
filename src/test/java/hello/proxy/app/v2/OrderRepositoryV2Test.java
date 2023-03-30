package hello.proxy.app.v2;

import hello.proxy.log.LogAppenders;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class OrderRepositoryV2Test extends LogAppenders {

    @Autowired
    private OrderRepositoryV2 repository;

    @Test
    @DisplayName("주문한 상품을 1초 후에 저장한다.")
    void saveTest() {
        //when
        ElapsedTimeChecker actual = new ElapsedTimeChecker(() ->
                repository.save("itemId"));
        //then
        assertThat(actual.elapsedTime()).isBetween(900L, 2000L);
        assertThat(getContainsLog("OrderRepositoryV2.save()")).isPresent();
        assertThat(getContainsLog("OrderRepositoryV2.save() time=")).isPresent();
    }

    @Test
    @DisplayName("주문한 상품 저장에 실패한다.")
    void saveFailTest() {
        assertThatThrownBy(() -> repository.save("ex"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(getContainsLog("OrderRepositoryV2.save()")).isPresent();
        assertThat(getContainsLog("OrderRepositoryV2.save() time=")).isPresent();
        assertThat(getContainsLog("ms ex=")).isPresent();
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
