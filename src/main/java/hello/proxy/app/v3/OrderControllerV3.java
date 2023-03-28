package hello.proxy.app.v3;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderControllerV3 {
    private final OrderServiceV3 service;

    public OrderControllerV3(OrderServiceV3 service) {
        this.service = service;
    }

    @GetMapping("/v3/request")
    public String request(@RequestParam("itemId") String itemId) {
        service.orderItem(itemId);
        return "ok";
    }

    @GetMapping("/v3/no-log")
    public String noLog() {
        return "ok";
    }
}
