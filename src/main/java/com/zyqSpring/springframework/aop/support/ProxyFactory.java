package com.zyqSpring.springframework.aop.support;

import com.zyqSpring.springframework.aop.AopProxy;
import com.zyqSpring.springframework.aop.CglibAopProxy;
import com.zyqSpring.springframework.aop.JdkDynamicAopProxy;

/**
 * Created by Enzo Cotter on 2021/6/1.
 */
public class ProxyFactory {
    public static AopProxy getProxy(AdvisedSupport advisedSupport, Class<?> targetClass) {
        if (targetClass.isInterface()) {
            return new JdkDynamicAopProxy(advisedSupport);
        } else {
            return new CglibAopProxy(advisedSupport);
        }
    }
}
