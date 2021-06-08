package org.zyqSpring.springframework.context;

import org.zyqSpring.springframework.core.factory.ZyqBeanFactory;

/**
 * 空接口，大家明白就好
 * 原接口需要继承ListableBeanFactory, HierarchicalBeanFactory等等，这里就简单继承BeanFactory
 **/
public interface ApplicationContext extends ZyqBeanFactory {
}
