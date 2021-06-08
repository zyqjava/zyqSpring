package org.zyqSpring.springframework.aop.framework;

/**
 * 创建代理的顶层接口
 */
public interface AopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
