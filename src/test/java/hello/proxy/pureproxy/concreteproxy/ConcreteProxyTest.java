package hello.proxy.pureproxy.concreteproxy;

import hello.proxy.log.LogAppenders;
import hello.proxy.pureproxy.concreteproxy.code.ConcreteClient;
import hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic;
import hello.proxy.pureproxy.concreteproxy.code.TimeProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcreteProxyTest extends LogAppenders {
    @Test
    @DisplayName("proxy 적용 전")
    void noProxy() {
        ConcreteLogic concreteLogic = new ConcreteLogic();
        ConcreteClient client = new ConcreteClient(concreteLogic);
        client.execute();

        assertThat(getContainsLog("ConcreteLogic 실행")).isPresent();
    }

    @Test
    @DisplayName("proxy 적용 후")
    void applyConcreteProxy() {
        ConcreteLogic concreteLogic = new ConcreteLogic();
        ConcreteLogic timeProxy = new TimeProxy(concreteLogic);
        ConcreteClient client = new ConcreteClient(timeProxy);
        client.execute();

        assertThat(getContainsLog("TimeDecorator 실행")).isPresent();
        assertThat(getContainsLog("TimeDecorator 종료 resultTime=")).isPresent();
        assertThat(getContainsLog("ConcreteLogic 실행")).isPresent();
    }
}
