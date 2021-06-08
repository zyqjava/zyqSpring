package org.zyqSpring.springframework.core.factory;

import org.zyqSpring.springframework.beans.config.ZyqBeanDefinition;
import org.zyqSpring.springframework.context.ZyqAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Enzo Cotter on 2021/5/21.
 */
public class ZyqDefaultListableBeanFactory extends ZyqAbstractApplicationContext {
    //存储注册信息的BeanDefinition
    protected final Map<String, ZyqBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, ZyqBeanDefinition>();
}
