package com.zyqSpring.springframework.beans.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 我们原来使用xml作为配置文件时，定义的Bean其实在IOC容器中被封装成了BeanDefinition，
 * 也就是Bean的配置信息，
 * 包括className、是否为单例、是否需要懒加载等等。它是一个interface，
 * 这里我们直接定义成class。
 *
 */
@Getter
@Setter
public class ZyqBeanDefinition {
    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;
    public ZyqBeanDefinition() {}
}
