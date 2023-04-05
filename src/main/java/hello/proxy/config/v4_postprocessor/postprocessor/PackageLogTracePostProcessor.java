package hello.proxy.config.v4_postprocessor.postprocessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
@RequiredArgsConstructor
public class PackageLogTracePostProcessor implements BeanPostProcessor {

    private final Advisor advisor;
    private final String basePackage;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //프록시 적용 대상 체크
        if (!isTargetBean(bean)) {
            return bean;
        }
        //프록시 적용 대상일 경우 프록시 팩토리 생성
        log.info("bean={}, beanName={}", bean, beanName);
        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvisor(advisor);
        Object proxy = factory.getProxy();
        log.info("create proxy: target={} proxy={}", bean.getClass(), proxy.getClass());
        return proxy;
    }

    private boolean isTargetBean(Object bean) {
        String packageName = bean.getClass().getPackageName();
        return packageName.startsWith(basePackage);
    }
}
