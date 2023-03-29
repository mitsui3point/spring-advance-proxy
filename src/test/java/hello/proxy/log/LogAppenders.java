package hello.proxy.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import hello.proxy.pureproxy.proxy.code.CacheProxy;
import hello.proxy.pureproxy.proxy.code.RealSubject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
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
        if (className.contains("Proxy")) {
            loggers.add(loggerContext.getLogger(CacheProxy.class));
            loggers.add(loggerContext.getLogger(RealSubject.class));
        }
        if (loggers.size() == 0) {
            throw new IllegalArgumentException("LogAppender 에서 지원되지 않는 클래스입니다.");
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
}