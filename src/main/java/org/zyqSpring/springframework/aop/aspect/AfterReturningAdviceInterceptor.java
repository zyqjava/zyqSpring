package org.zyqSpring.springframework.aop.aspect;

import org.zyqSpring.springframework.aop.intercept.MethodInterceptor;
import org.zyqSpring.springframework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 后置通知
 */
public class AfterReturningAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    private JoinPoint joinPoint;

    public AfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void afterReturning(Object retVal, Method method, Object [] args, Object target) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, retVal, null);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //先调用下一个拦截器
        Object retVal = invocation.proceed();
        //再调用后置通知
        this.joinPoint = invocation;
        this.afterReturning(retVal, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return retVal;
    }
}
