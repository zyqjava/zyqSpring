package com.zyqSpring.springframework.aop.framework;

import com.zyqSpring.springframework.aop.support.AdvisedSupport;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Enzo Cotter on 2021/5/24.
 */
public class CglibAopProxy implements AopProxy, InvocationHandler {

    private Enhancer enhancer = new Enhancer();

    private AdvisedSupport advisedSupport;

    public CglibAopProxy(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advisedSupport.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        //设置需要创建子类的类
        enhancer.setSuperclass(classLoader.getClass());
        //通过字节码技术动态创建子类实例
        return enhancer.create();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
