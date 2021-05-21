package com.zyqSpring.springframework.beans.config;

/**
 * Created by Enzo Cotter on 2021/5/21.
 */
public class ZyqBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
