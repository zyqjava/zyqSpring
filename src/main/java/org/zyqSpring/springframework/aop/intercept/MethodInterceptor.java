package org.zyqSpring.springframework.aop.intercept;

/**
 * Created by Enzo Cotter on 2021/5/24.
 */
public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}

