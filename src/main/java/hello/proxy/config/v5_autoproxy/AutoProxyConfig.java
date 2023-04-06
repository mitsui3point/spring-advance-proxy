package hello.proxy.config.v5_autoproxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 중요: 포인트컷은 2가지에 사용된다.
 *
 * 1. 프록시 적용 여부 판단 - 생성 단계
 *  자동 프록시 생성기는 포인트컷을 사용해서 해당 빈이 프록시를 생성할 필요가 있는지 없는지 체크한다.
 *  클래스 + 메서드 조건을 모두 비교한다. 이때 모든 메서드를 체크하는데, 포인트컷 조건에 하나하나 매칭해본다.
 *  만약 조건에 맞는 것이 하나라도 있으면 프록시를 생성한다.
 *  예) orderControllerV1 은 request() , noLog() 가 있다. 여기에서 request() 가 조건에 만족하므로 프록시를 생성한다.
 *      만약 조건에 맞는 것이 하나도 없으면 프록시를 생성할 필요가 없으므로 프록시를 생성하지 않는다.
 *
 * 2. 어드바이스 적용 여부 판단 - 사용 단계
 *  프록시가 호출되었을 때 부가 기능인 어드바이스를 적용할지 말지 포인트컷을 보고 판단한다.
 *  앞서 설명한 예에서 orderControllerV1 은 이미 프록시가 걸려있다.
 *  orderControllerV1 의 request() 는 현재 포인트컷 조건에 만족하므로 프록시는 어드바이스를 먼저 호출하고, target 을 호출한다.
 *  orderControllerV1 의 noLog() 는 현재 포인트컷 조건에 만족하지 않으므로 어드바이스를 호출하지 않고 바로 target 만 호출한다.
 *  참고: 프록시를 모든 곳에 생성하는 것은 비용 낭비이다. 꼭 필요한 곳에 최소한의 프록시를 적용해야 한다.
 *  그래서 자동 프록시 생성기는 모든 스프링 빈에 프록시를 적용하는 것이 아니라 포인트컷으로 한번 필터링해서 어드바이스가 사용될 가능성이 있는 곳에만 프록시를 생성한다.
 */
@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class AutoProxyConfig {
    public static final String[] POINTCUT_MAPPED_NAMES = {"request*", "order*", "save*"};

    //@Bean
    public Advisor advisor1(LogTrace trace) {
        //pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames(POINTCUT_MAPPED_NAMES);

        //advice
        LogTraceAdvice advice = new LogTraceAdvice(trace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    //@Bean
    public Advisor advisor2(LogTrace trace) {
        //pointcut
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();//AspectJ 포인트컷 표현식을 적용할 수 있다.
        pointcut.setExpression("execution(* hello.proxy.app..*(..))");//* (모든 반환 타입), hello.proxy.app..(해당 패키지와 그 하위 패키지) *(..)(*;모든 메서드 이름, (..);어떤 파라미터던 상관없다)

        //advice
        LogTraceAdvice advice = new LogTraceAdvice(trace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    @Bean
    public Advisor advisor3(LogTrace trace) {
        //pointcut
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();//AspectJ 포인트컷 표현식을 적용할 수 있다.
        pointcut.setExpression("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..)) ");//* (모든 반환 타입), hello.proxy.app..(해당 패키지와 그 하위 패키지) noLog(..)(noLog;메서드 이름, (..);어떤 파라미터던 상관없다)

        //advice
        LogTraceAdvice advice = new LogTraceAdvice(trace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }
}
