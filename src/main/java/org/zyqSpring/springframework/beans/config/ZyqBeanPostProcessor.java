package org.zyqSpring.springframework.beans.config;

/**
 * Created by Enzo Cotter on 2021/5/21.
 */
public class ZyqBeanPostProcessor implements BeanPostProcessor{

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
