package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.Controller;

/**
 * {@link RequestMapping}
 * : 스프링 MVC 는 {@link Controller} 또는 {@link RequestMapping} 애노테이션이 타입에 있어야 스프링 컨트롤러로 인식한다.
 *   그리고 스프링 컨트롤러로 인식해야, HTTP URL 이 매핑되고 동작한다. 이 애노테이션은 인터페이스에 사용해도 된다.
 * {@link ResponseBody}
 * : HTTP 메시지 컨버터를 사용해서 응답한다. 이 애노테이션은 인터페이스에 사용해도 된다.
 *
 * 코드를 보면 request(), noLog() 두 가지 메서드가 있다.
 * request()는 LogTrace 를 적용할 대상이고,
 * noLog()는 LogTrace 를 적용하지 않을 대상이다.
 */
@RequestMapping//스프링은 @Controller 또는 @RequestMapping 이 있어야 스프링 컨트롤러로 인식
@ResponseBody
public interface OrderControllerV1 {
    @GetMapping("/v1/request")
    String request(@RequestParam("itemId") String itemId);//인터페이스에서 @RequestParam 사용시 param 의 key 값을 명시해주어야 오류가 생길 소지가 없다.
    @GetMapping("/v1/no-log")
    String noLog();
}
