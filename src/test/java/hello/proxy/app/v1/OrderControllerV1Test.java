package hello.proxy.app.v1;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerV1Test extends LogAppenders {

    public static final String REQ_URL = "/v1/request";
    public static final String NO_LOG_URL = "/v1/no-log";

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
        assertThat(getContainsLog("OrderControllerV1.request()")).isPresent();
        assertThat(getContainsLog("|-->OrderServiceV1.orderItem()")).isPresent();
        assertThat(getContainsLog("|   |-->OrderRepositoryV1.save()")).isPresent();
        assertThat(getContainsLog("|   |<--OrderRepositoryV1.save() time=")).isPresent();
        assertThat(getContainsLog("|<--OrderServiceV1.orderItem() time=")).isPresent();
        assertThat(getContainsLog("OrderControllerV1.request() time=")).isPresent();
    }

    @Test
    @DisplayName("요청 API 를 호출을 실패한다.")
    void requestFailTest() {
        //then
        assertThatThrownBy(() -> {
            //when
            ResultActions perform = mvc.perform(get(REQ_URL)
                    .param("itemId", "ex"));
        }).hasCause(new IllegalArgumentException("예외 발생"));
        assertThat(getContainsLog("OrderControllerV1.request()")).isPresent();
        assertThat(getContainsLog("|-->OrderServiceV1.orderItem()")).isPresent();
        assertThat(getContainsLog("|   |-->OrderRepositoryV1.save()")).isPresent();
        assertThat(getContainsLog("|   |<X-OrderRepositoryV1.save() time=")).isPresent();
        assertThat(getContainsLog("|<X-OrderServiceV1.orderItem() time=")).isPresent();
        assertThat(getContainsLog("OrderControllerV1.request() time=")).isPresent();
    }

    @Test
    @DisplayName("No Log API 를 호출하여 ok 를 리턴받는다.")
    void noLogTest() throws Exception {
        //when
        ResultActions perform = mvc.perform(get(NO_LOG_URL));
        //then
        perform.andDo(print())
                .andExpect(content().string("ok"));
        assertThat(getContainsLog("OrderControllerV1.noLog()")).isNotPresent();
    }
}
