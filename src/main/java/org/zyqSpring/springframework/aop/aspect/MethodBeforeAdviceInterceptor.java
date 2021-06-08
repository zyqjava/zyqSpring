package org.zyqSpring.springframework.aop.aspect;

import org.zyqSpring.springframework.aop.intercept.MethodInterceptor;
import org.zyqSpring.springframework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 拦截器本质上就是各种通知方法的封装，因此继承AbstractAspectAdvice，
 * 实现MethodInterceptor。
 * 实现前置通知
 */
public class MethodBeforeAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    private JoinPoint joinPoint;

    public MethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object [] args, Object target) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, null, null);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        this.joinPoint = invocation;
        //再调用下一个拦截器前先执行前置通知
        before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return invocation.proceed();
    }
}
