package com.zyqSpring.springframework.core.factory;

import com.zyqSpring.springframework.beans.config.ZyqBeanDefinition;
import com.zyqSpring.springframework.context.ZyqAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Enzo Cotter on 2021/5/21.
 */
public class ZyqDefaultListableBeanFactory extends ZyqAbstractApplicationContext {
    //存储注册信息的BeanDefinition
    protected final Map<String, ZyqBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, ZyqBeanDefinition>();
}
