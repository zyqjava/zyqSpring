package com.zyqSpring.springframework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 写一个抽象子类来封装不同的通知类型的共同逻辑：
 * 调用通知方法前先将入参赋值，这样用户在写通知方法时才能拿到实际值。
 */
public abstract class AbstractAspectAdvice implements Advice{

    /*通知方法*/
    private Method aspectMethod;

    /*切面类*/
    private Object aspectTarget;

    public AbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    /*
    * 调用通知方法
    * */
    public Object invokeAdviceMethod(JoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable{
        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        if (null == parameterTypes || parameterTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < args.length; i++) {
                if (parameterTypes[i] == JoinPoint.class) {
                    args[i] = joinPoint;
                } else if (parameterTypes[i] == Throwable.class) {
                    args[i] = tx;
                } else if (parameterTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }
}
