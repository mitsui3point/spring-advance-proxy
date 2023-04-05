package hello.proxy.config.v4_postprocessor;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.config.v4_postprocessor.postprocessor.PackageLogTracePostProcessor;
import hello.proxy.trace.logtrace.LogTrace;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * V1 : jdkDynamicProxy
 * V3, V2 : CGLIB
 */
@Configuration
@Import({AppV1Config.class, AppV2Config.class})//V3는 컴포넌트 스캔으로 자동으로 스프링 빈 등록이 되지만, V1, V2 애플리케이션은 수동으로 스프링 빈을 등록해야 동작한다.
public class BeanPostProcessorConfig {

    private static final String HELLO_PROXY_APP = "hello.proxy.app";
    private static final String[] POINTCUT_MAPPED_NAMES = {"request*", "order*", "save*"};

    @Bean
    public BeanPostProcessor packageLogTracePostProcessor(LogTrace trace) {
        return new PackageLogTracePostProcessor(logTraceAdvisor(trace), HELLO_PROXY_APP);
    }

    public Advisor logTraceAdvisor(LogTrace trace) {
        //pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames(POINTCUT_MAPPED_NAMES);
        //advice
        Advice advice = new LogTraceAdvice(trace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }
}
