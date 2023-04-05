package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 빈 후처리기 - BeanPostProcessor
 * 스프링이 빈 저장소에 등록할 목적으로 생성한 객체를 빈 저장소에 등록하기 직전에 조작하고 싶다면 빈
 * 후처리기를 사용하면 된다.
 * 빈 포스트 프로세서( BeanPostProcessor )는 번역하면 빈 후처리기인데, 이름 그대로 빈을 생성한 후에
 * 무언가를 처리하는 용도로 사용한다.
 */
public class BeanPostProcessorTest {
    @Test
    void beanPostProcessorConfig() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanPostProcessorConfig.class);
        //A는 빈으로 등록된다. -> 후처리기가 new A() -> new B() 로 인스턴스를 바꿔치기 한다.
        B b = applicationContext.getBean("beanA", B.class);
        b.helloB();

        //A 인스턴스는 빈으로 등록되지 않는다.
        assertThatThrownBy(() -> applicationContext.getBean(A.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class);
    }

    @Slf4j
    @Configuration
    static class BeanPostProcessorConfig {
        @Bean(name="beanA")
        public A a() {
            return new A();
        }

        @Bean
        public AToBPostProcessor helloPostProcessor() {
            return new AToBPostProcessor();
        }
    }

    @Slf4j
    static class A {
        public void helloA() {
            log.info("hello A");
        }
    }

    @Slf4j
    static class B {
        public void helloB() {
            log.info("hello B");
        }
    }

    /**
     * AToBPostProcessor
     *  빈 후처리기이다.
     *  인터페이스인 BeanPostProcessor 를 구현하고, 스프링 빈으로 등록하면 스프링 컨테이너가 빈 후처리기로 인식하고 동작한다.
     *  이 빈 후처리기는 A 객체를 새로운 B 객체로 바꿔치기 한다.
     *  파라미터로 넘어오는 빈( bean ) 객체가 A 의 인스턴스이면 새로운 B 객체를 생성해서 반환한다.
     *  여기서 A 대신에 반환된 값인 B 가 스프링 컨테이너에 등록된다.
     *  다음 실행결과를 보면 beanName=beanA, bean=A 객체의 인스턴스가 빈 후처리기에 넘어온 것을 확인할 수 있다
     */
    @Slf4j
    static class AToBPostProcessor implements BeanPostProcessor {

        /**
         * 초기화 후 후킹하여 처리할 메서드
         */
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            log.info("beanName={} bean={}", beanName, bean);
            if (bean instanceof A) {
                return new B();
            }
            return bean;
        }
    }
}
