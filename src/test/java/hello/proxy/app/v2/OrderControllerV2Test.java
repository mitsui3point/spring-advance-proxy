package hello.proxy.app.v2;

import hello.proxy.log.LogAppenders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerV2Test extends LogAppenders {

    public static final String REQ_URL = "/v2/request";
    public static final String NO_LOG_URL = "/v2/no-log";

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

        assertRequestLog(2, false);
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
        assertRequestLog(2, true);
    }

    @Test
    @DisplayName("No Log API 를 호출하여 ok 를 리턴받는다.")
    void noLogTest() throws Exception {
        //when
        ResultActions perform = mvc.perform(get(NO_LOG_URL));
        //then
        perform.andDo(print())
                .andExpect(content().string("ok"));
        assertThat(getContainsLog("OrderControllerV2.noLog()")).isNotPresent();
    }
}
