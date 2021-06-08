package org.zyqSpring.springframework.aop.framework;

import org.zyqSpring.springframework.aop.intercept.MethodInvocation;
import org.zyqSpring.springframework.aop.support.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * spring选择代理创建逻辑是，如果被代理的类有实现接口用原生JDK的动态代理，
 * 否则使用Cglib的动态代理
 * 是利用JDK动态代理来创建代理的，因此需实现InvocationHandler接口。
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private AdvisedSupport advisedSupport;

    public JdkDynamicAopProxy(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advisedSupport.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.advisedSupport.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获取拦截器链
        List<Object> interceptorsAndDynamicInterceptionAdvice =
                this.advisedSupport.getInterceptorsAndDynamicInterceptionAdvice(method, this.advisedSupport.getTargetClass());
        //外层拦截器，用于控制拦截器链的执行
        MethodInvocation methodInvocation = new MethodInvocation(
                proxy,
                this.advisedSupport.getTarget(),
                method,
                args,
                this.advisedSupport.getTargetClass(),
                interceptorsAndDynamicInterceptionAdvice);
        //开始连接器链的调用
        return methodInvocation.proceed();
    }
}
