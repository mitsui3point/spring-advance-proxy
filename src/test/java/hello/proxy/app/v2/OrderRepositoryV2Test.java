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
        ElapsedTimeChecker actual = new ElapsedTimeChecker(() -> repository.save("itemId"));
        //then
        assertThat(actual.elapsedTime()).isBetween(900L, 2000L);
        assertSaveLog(2, false);
    }

    @Test
    @DisplayName("주문한 상품 저장에 실패한다.")
    void saveFailTest() {
        assertThatThrownBy(() -> repository.save("ex"))
                .isInstanceOf(IllegalArgumentException.class);
        assertSaveLog(2, true);
    }
}
