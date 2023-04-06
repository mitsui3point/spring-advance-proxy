package hello.proxy.config.v6_aop.aspect;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Method;

@Slf4j
@Aspect//애노테이션 기반 프록시를 적용할 때 필요하다//Advisor(pointcut+advice) 를 편리하게 생성해주는 기능을 제공
@RequiredArgsConstructor
public class LogTraceAspect {
    private final LogTrace trace;

    @Around("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))")//@Around 의 value 에 pointcut 의 표현식을 넣는다. 표현식은 AspectJ 표현식을 사용한다. @Around 의 메서드는 어드바이스(Advice) 가 된다.
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {// ProceedingJoinPoint; 어드바이스에서 살펴본 MethodInvocation invocation 과 유사한 기능이다. 내부에 실제 호출 대상, 전달 인자, 그리고 어떤 객체와 어떤 메서드가 호출되었는지 정보가 포함되어 있다.
        //advice
        TraceStatus status = null;

        //log.info("target={}", joinPoint.getTarget());
        //log.info("getArgs={}", joinPoint.getArgs());
        //log.info("getSignature={}", joinPoint.getSignature());

        try {
            String message = joinPoint.getSignature().toShortString();
            status = trace.begin(message);

            //로직 호출
            Object result = joinPoint.proceed();//실제 호출 대상(target) 을 호출한다.

            trace.end(status);
            return result;
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}
