package hello.proxy.app.v3;

import hello.proxy.log.LogAppenders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerV3Test extends LogAppenders {

    public static final String REQ_URL = "/v3/request";
    public static final String NO_LOG_URL = "/v3/no-log";

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("요청 API 를 호출하여 ok 를 리턴받는다.")
    void requestTest() throws Exception {
        //when
        ResultActions perform = mvc.perform(get(REQ_URL)
                .param("itemId", "itemId"));
        //then
        perform.andDo(print())
                .andExpect(content().string("ok"));

        assertThat(getOrderedLogs().get(0)).contains("OrderControllerV3.request()");
        assertThat(getOrderedLogs().get(1)).contains("|-->OrderServiceV3.orderItem()");
        assertThat(getOrderedLogs().get(2)).contains("|   |-->OrderRepositoryV3.save()");
        assertThat(getOrderedLogs().get(3)).contains("|   |<--OrderRepositoryV3.save() time=");
        assertThat(getOrderedLogs().get(4)).contains("|<--OrderServiceV3.orderItem() time=");
        assertThat(getOrderedLogs().get(5)).contains("OrderControllerV3.request() time=");
    }

    @Test
    @DisplayName("요청 API 를 호출을 실패한다.")
    void requestFailTest() throws Exception {
        //when
        assertThatThrownBy(() -> {
            //then
            mvc.perform(get(REQ_URL)
                    .param("itemId", "ex"));
        }).hasCause(new IllegalArgumentException("예외 발생"));

        assertThat(getOrderedLogs().get(0)).contains("OrderControllerV3.request()");
        assertThat(getOrderedLogs().get(1)).contains("|-->OrderServiceV3.orderItem()");
        assertThat(getOrderedLogs().get(2)).contains("|   |-->OrderRepositoryV3.save()");
        assertThat(getOrderedLogs().get(3)).contains("|   |<X-OrderRepositoryV3.save() time=");
        assertThat(getOrderedLogs().get(4)).contains("|<X-OrderServiceV3.orderItem() time=");
        assertThat(getOrderedLogs().get(5)).contains("OrderControllerV3.request() time=");
    }

    @Test
    @DisplayName("No Log API 를 호출하여 ok 를 리턴받는다.")
    void noLogTest() throws Exception {
        //when
        ResultActions perform = mvc.perform(get(NO_LOG_URL));
        //then
        perform.andDo(print())
                .andExpect(content().string("ok"));
    }
}
