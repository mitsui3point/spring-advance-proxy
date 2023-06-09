package hello.proxy.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.jdkdynamic.ReflectionTest;
import hello.proxy.jdkdynamic.code.AImpl;
import hello.proxy.jdkdynamic.code.BImpl;
import hello.proxy.jdkdynamic.code.TimeInvocationHandler;
import hello.proxy.pureproxy.concreteproxy.code.ConcreteLogic;
import hello.proxy.pureproxy.concreteproxy.code.TimeProxy;
import hello.proxy.pureproxy.decorator.code.DecoratorPatternClient;
import hello.proxy.pureproxy.decorator.code.MessageDecorator;
import hello.proxy.pureproxy.decorator.code.RealComponent;
import hello.proxy.pureproxy.decorator.code.TimeDecorator;
import hello.proxy.pureproxy.proxy.code.CacheProxy;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import hello.proxy.trace.logtrace.ThreadLocalLogTrace;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LogAppenders {

    protected ListAppender<ILoggingEvent> listAppender;
    private List<Logger> loggers;
    private LoggerContext loggerContext;

    @BeforeEach
    void setUp() {
        setLogAppenderInfo();
        logAppendStart();
    }

    @AfterEach
    public void tearDown() {
        logAppendEnd();
    }

    private void setLogAppenderInfo() {
        String className = this.getClass().getName();
        loggers = new ArrayList<>();
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggers.add(loggerContext.getLogger(this.getClass()));
        if (className.contains("V1") ||
                className.contains("V2") ||
                className.contains("V3")) {
            loggers.add(loggerContext.getLogger(ThreadLocalLogTrace.class));
        }
        if (className.contains("ProxyTest")) {
            loggers.add(loggerContext.getLogger(CacheProxy.class));
            loggers.add(loggerContext.getLogger(RealSubject.class));
        }
        if (className.contains("DecoratorTest")) {
            loggers.add(loggerContext.getLogger(RealComponent.class));
            loggers.add(loggerContext.getLogger(DecoratorPatternClient.class));
            loggers.add(loggerContext.getLogger(MessageDecorator.class));
            loggers.add(loggerContext.getLogger(TimeDecorator.class));
        }
        if (className.contains("ConcreteProxyTest")) {
            loggers.add(loggerContext.getLogger(ConcreteLogic.class));
            loggers.add(loggerContext.getLogger(TimeProxy.class));
        }
        if (className.contains("ReflectionTest")) {
            loggers.add(loggerContext.getLogger(ReflectionTest.class));
        }
        if (className.contains("JdkDynamicProxyTest")) {
            loggers.add(loggerContext.getLogger(TimeInvocationHandler.class));
            loggers.add(loggerContext.getLogger(AImpl.class));
            loggers.add(loggerContext.getLogger(BImpl.class));
        }
        if (className.contains("CglibTest")) {
            loggers.add(loggerContext.getLogger(TimeMethodInterceptor.class));
            loggers.add(loggerContext.getLogger(ConcreteService.class));
        }
        if (className.contains("ProxyFactoryTest")) {
            loggers.add(loggerContext.getLogger(TimeAdvice.class));
            loggers.add(loggerContext.getLogger(ServiceImpl.class));
            loggers.add(loggerContext.getLogger(ConcreteService.class));
        }
        if (className.contains("AdvisorTest")) {
            loggers.add(loggerContext.getLogger(TimeAdvice.class));
            loggers.add(loggerContext.getLogger(ServiceImpl.class));
        }
        if (loggers.size() == 0) {
            throw new IllegalArgumentException("LogAppenders 에서 지원되지 않는 클래스입니다.");
        }
        listAppender = new ListAppender<>();
    }

    private void logAppendStart() {
        listAppender.start();
        loggers.stream().forEach(o -> o.addAppender(listAppender));
    }

    private void logAppendEnd() {
        loggers.stream().forEach(o -> o.detachAppender(listAppender));
        listAppender.stop();
    }

    protected Optional<ILoggingEvent> getContainsLog(String expectedLog) {
        return listAppender.list
                .stream()
                .filter(o -> {
                    if (o != null) return o.getFormattedMessage().contains(expectedLog);
                    return false;
                })
                .findAny();
    }

    protected List<String> getOrderedLogs() {
        if (ObjectUtils.isEmpty(listAppender.list)) {
            return null;
        }
        List<String> logs = listAppender.list
                .stream()
                .map(o -> o.toString())
                .collect(Collectors.toList());
        return logs;
    }

    protected void assertRequestLog(int appVersion, boolean isThrownException) {
        assertThat(getOrderedLogs().get(0)).contains("OrderControllerV"+appVersion).contains(".request");
        assertThat(getOrderedLogs().get(1)).contains("|-->OrderServiceV"+appVersion).contains(".orderItem");
        assertThat(getOrderedLogs().get(2)).contains("|   |-->OrderRepositoryV"+appVersion).contains(".save");
        if (isThrownException) {
            assertThat(getOrderedLogs().get(3)).contains("|   |<X-OrderRepositoryV"+appVersion).contains(".save").contains("time=").contains("ex=");
            assertThat(getOrderedLogs().get(4)).contains("|<X-OrderServiceV"+appVersion).contains(".orderItem").contains("time=").contains("ex=");
            assertThat(getOrderedLogs().get(5)).contains("OrderControllerV"+appVersion).contains(".request").contains("time=").contains("ex=");
            return;
        }
        assertThat(getOrderedLogs().get(3)).contains("|   |<--OrderRepositoryV"+appVersion).contains(".save").contains("time=");
        assertThat(getOrderedLogs().get(4)).contains("|<--OrderServiceV"+appVersion).contains(".orderItem").contains("time=");
        assertThat(getOrderedLogs().get(5)).contains("OrderControllerV"+appVersion).contains(".request").contains("time=");
    }

    protected void assertOrderItemLog(int appVersion, boolean isThrownException) {
        assertThat(getOrderedLogs().get(0)).contains("OrderServiceV"+appVersion).contains(".orderItem");
        assertThat(getOrderedLogs().get(1)).contains("|-->OrderRepositoryV"+appVersion).contains(".save");
        if (isThrownException) {
            assertThat(getOrderedLogs().get(2)).contains("|<X-OrderRepositoryV"+appVersion).contains(".save").contains("time=").contains("ex=");
            assertThat(getOrderedLogs().get(3)).contains("OrderServiceV"+appVersion).contains(".orderItem").contains("time=").contains("ex=");
            return;
        }
        assertThat(getOrderedLogs().get(2)).contains("|<--OrderRepositoryV"+appVersion).contains(".save").contains("time=");
        assertThat(getOrderedLogs().get(3)).contains("OrderServiceV"+appVersion).contains(".orderItem").contains("time=");
    }

    protected void assertSaveLog(int appVersion, boolean isThrownException) {
        assertThat(getOrderedLogs().get(0)).contains("OrderRepositoryV"+appVersion).contains(".save");
        if (isThrownException) {
            assertThat(getOrderedLogs().get(1)).contains("OrderRepositoryV"+appVersion).contains(".save").contains("time=").contains("ex=");
            return;
        }
        assertThat(getOrderedLogs().get(1)).contains("OrderRepositoryV"+appVersion).contains(".save").contains("time=");
    }

    @RequiredArgsConstructor
    protected static class ElapsedTimeChecker {
        private final Callback callback;
        public long elapsedTime() {
            long startTime = System.currentTimeMillis();
            callback.call();
            return System.currentTimeMillis() - startTime;
        }
    }

    protected static interface Callback {
        void call();
    }
}