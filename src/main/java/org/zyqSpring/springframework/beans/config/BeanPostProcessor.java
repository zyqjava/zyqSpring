package org.zyqSpring.springframework.beans.config;

/**
 * Created by Enzo Cotter on 2021/6/7.
 */
public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName);
    Object postProcessAfterInitialization(Object bean, String beanName);
}
