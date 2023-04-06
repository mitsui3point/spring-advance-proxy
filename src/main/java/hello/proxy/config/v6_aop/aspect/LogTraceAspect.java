package hello.proxy.config.v6_aop.aspect;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.aspectj.annotation.BeanFactoryAspectJAdvisorsBuilder;

/**
 * {@link Aspect} 를 어드바이저로 변환해서 저장하는 과정
 * 1. 실행: 스프링 애플리케이션 로딩 시점에 자동 프록시 생성기를 호출한다.
 * 2. 모든 @Aspect bean 조회: 자동 프록시 생성기{@link AnnotationAwareAspectJAutoProxyCreator} 는 @Aspect 애노테이션이 붙은 스프링 빈을 모두 조회한다.
 * 3. 어드바이저 생성: @Aspect 어드바이저 빌더를 통해 @Aspect 애노테이션 정보를 기반으로 어드바이저를 생성한다.
 * 4. @Aspect 기반 어드바이저 저장: 생성한 어드바이저를 @Aspect 어드바이저 빌더 {@link BeanFactoryAspectJAdvisorsBuilder} 내부에 저장한다.
 *
 * {@link BeanFactoryAspectJAdvisorsBuilder}: @Aspect 어드바이저 빌더
 * : @Aspect 의 정보를 기반으로 포인트컷, 어드바이스, 어드바이저를 생성하고 보관하는 역할을 담당한다.
 * : @Aspect 의 정보를 기반으로 어드바이저를 만들고, @Aspect 어드바이저 빌더 내부 저장소에 캐시한다.
 * : 캐시에 어드바이저가 이미 만들어져 있는 경우 캐시에 저장된 어드바이저를 반환한다.
 */
@Slf4j
@Aspect//애노테이션 기반 프록시를 적용할 때 필요하다//Advisor(pointcut+advice) 를 편리하게 생성해주는 기능을 제공
@RequiredArgsConstructor
public class LogTraceAspect {
    private final LogTrace trace;

    /**
     * : @Around; Advisor
     * : @Around(value="~~"); Pointcut
     * : method logic; Advice logic
     */
    @Around(value = "execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))")//@Around 의 value 에 pointcut 의 표현식을 넣는다. 표현식은 AspectJ 표현식을 사용한다. @Around 의 메서드는 어드바이스(Advice) 가 된다.
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {// ProceedingJoinPoint; 어드바이스에서 살펴본 MethodInvocation invocation 과 유사한 기능이다. 내부에 실제 호출 대상, 전달 인자, 그리고 어떤 객체와 어떤 메서드가 호출되었는지 정보가 포함되어 있다.

        //Advice 로직
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
