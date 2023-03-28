package hello.proxy.app.v2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping//사용 이유, @Controller 사용 시 자등으로 component scan 대상이 되므로 수동 빈 등록에 영향이 가지 않도록 하기 위해ㅐ
@ResponseBody
public class OrderControllerV2 {
    private final OrderServiceV2 service;

    public OrderControllerV2(OrderServiceV2 service) {
        this.service = service;
    }

    @GetMapping("/v2/request")
    public String request(@RequestParam String itemId) {
        service.orderItem(itemId);
        return "ok";
    }

    @GetMapping("/v2/no-log")
    public String noLog() {
        return "ok";
    }
}
