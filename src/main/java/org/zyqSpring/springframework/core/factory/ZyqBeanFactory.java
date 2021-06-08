package org.zyqSpring.springframework.core.factory;

/**
 * 是IOC容器的顶层父接口，大名鼎鼎的 ApplicationContext就是继承它，它定义了我们最常用的获取Bean的方法。
 */
public interface ZyqBeanFactory {

    Object getBean(String name) throws Exception;

    <T> T getBean(Class<T> requiredType) throws Exception;
}
