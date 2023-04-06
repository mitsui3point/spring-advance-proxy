package hello.proxy.config.v6_aop;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v6_aop.aspect.LogTraceAspect;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 자동 프록시 생성기({@link AnnotationAwareAspectJAutoProxyCreator}) - 빈 후처리기 의 작동 과정
 * 1. 생성: 스프링 빈 대상이 되는 객체를 생성한다. ( @Bean , 컴포넌트 스캔 모두 포함)
 * 2. 전달: 생성된 객체를 빈 저장소에 등록하기 직전에 빈 후처리기에 전달한다.
 * 3-1. Advisor 빈 조회: 스프링 컨테이너에서 Advisor 빈을 모두 조회한다.
 * 3-2. @Aspect Advisor 조회: @Aspect 어드바이저 빌더 내부에 저장된 Advisor 를 모두 조회한다.
 * 4. 프록시 적용 대상 체크:
 *      앞서 3-1, 3-2에서 조회한 Advisor 에 포함되어 있는 포인트컷을 사용해서 해당 객체가 프록시를 적용할 대상인지 아닌지 판단한다.
 *      이때 객체의 클래스 정보는 물론이고, 해당 객체의 모든 메서드를 포인트컷에 하나하나 모두 매칭해본다.
 *      그래서 조건이 하나라도 만족하면 프록시 적용 대상이 된다.
 *      예를 들어서 메서드 하나만 포인트컷 조건에 만족해도 프록시 적용 대상이 된다.
 * 5. 프록시 생성:
 *      프록시 적용 대상이면 프록시를 생성하고 프록시를 반환한다.
 *      그래서 프록시를 스프링 빈으로 등록한다.
 *      만약 프록시 적용 대상이 아니라면 원본 객체를 반환해서 원본 객체를 스프링 빈으로 등록한다.
 * 6. 빈 등록: 반환된 객체는 스프링 빈으로 등록된다.
 *
 * : @Aspect 를 사용해서 애노테이션 기반 프록시를 매우 편리하게 적용해보았다.
 *   실무에서 프록시를 적용할 때는 대부분이 이 방식을 사용한다.
 * : 지금까지 우리가 진행한 애플리케이션 전반에 로그를 남기는 기능은 특정 기능 하나에 관심이 있는 기능이 아니다.
 *   애플리케이션의 여러 기능들 사이에 걸쳐서 들어가는 관심사이다.
 *   이것을 바로 횡단 관심사(cross-cutting concerns)라고 한다.
 *   지금까지 진행한 방법이 이렇게 여러곳에 걸쳐 있는 횡단 관심사의 문제를 해결하는 방법이었다.
 */
@Slf4j
@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class AopConfig {
    @Bean
    public LogTraceAspect logTraceAspect(LogTrace trace) {
        return new LogTraceAspect(trace);
    }
}
