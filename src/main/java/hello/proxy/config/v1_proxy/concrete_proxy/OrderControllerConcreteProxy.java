package hello.proxy.config.v1_proxy.concrete_proxy;

import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;

public class OrderControllerConcreteProxy extends OrderControllerV2 {
    private final OrderControllerV2 target;
    private final LogTrace trace;

    public OrderControllerConcreteProxy(OrderControllerV2 target, LogTrace trace) {
        super(null);//부모타입 생성자가 항상 존재해야 한다. 여기서는 부모타입 생성자 안쓸예정이므로 null
        this.target = target;
        this.trace = trace;
    }

    @Override
    public String request(String itemId) {
        String result = "";
        TraceStatus status = null;

        try {
            status = trace.begin("OrderControllerV2.request()");
            result = target.request(itemId);
            trace.end(status);
            return result;
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }

    @Override
    public String noLog() {
        return target.noLog();
    }
}
