package com.zyqSpring.springframework.aop.support;

import com.zyqSpring.springframework.aop.aspect.AfterReturningAdviceInterceptor;
import com.zyqSpring.springframework.aop.aspect.AfterThrowingAdviceInterceptor;
import com.zyqSpring.springframework.aop.aspect.MethodBeforeAdviceInterceptor;
import com.zyqSpring.springframework.aop.config.AopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 成员变量AdvisedSupport
 * 封装了创建代理所需要的一切资源
 * 被代理的目标实例、拦截器链等，实际上它还负责解析AOP配置和创建拦截器
 */
public class AdvisedSupport {

    /**被代理的类class*/
    private Class<?> targetClass;

    /**被代理的对象实例*/
    private Object target;

    /**被代理的方法对应的拦截器集合*/
    private transient Map<Method, List<Object>> methodCache;

    /**AOP外部配置*/
    private AopConfig aopConfig;

    /**切点正则表达式*/
    private Pattern pointCutClassPattern;

    public AdvisedSupport(AopConfig aopConfig) {
        this.aopConfig = aopConfig;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * 获取拦截器
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> cached = methodCache.get(method);
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    /**
     * 解析AOP配置，创建拦截器
     */
    private void parse() {
        //编译切点表达式为正则
        String pointCut = aopConfig.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        //pointCut=public .* com.zyqSpring.demo.service..*Service..*(.*)
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));
        try {
            //保存切面的所有通知方法
            Map<String, Method> aspectMethods = new HashMap<>();
            //获取切面类所有方法
            Class<?> aspectClass = Class.forName(this.aopConfig.getAspectClass());
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }

            //遍历被代理类的所有方法，为符合切点表达式的方法创建拦截器
            methodCache = new HashMap<>();
            Pattern pattern = Pattern.compile(pointCut);
            for (Method method : this.targetClass.getMethods()) {
                String methodString = method.toString();
                //为了能正确匹配,这里去除函数签名尾部的throws xxxException
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pattern.matcher(methodString);
                if (matcher.matches()) {
                    //执行器链
                    List<Object> advices = new LinkedList<>();

                    //创建前置拦截器
                    if (!(null ==  aopConfig.getAspectBefore() || "".equals(aopConfig.getAspectBefore()))) {
                        //创建一个advice
                        MethodBeforeAdviceInterceptor beforeAdviceInterceptor = new MethodBeforeAdviceInterceptor(aspectMethods.get(aopConfig.getAspectBefore()), aspectClass.newInstance());
                        advices.add(beforeAdviceInterceptor);
                    }
                    //创建后置拦截器
                    if (!(null ==  aopConfig.getAspectAfter() || "".equals(aopConfig.getAspectAfter()))) {
                        //创建一个advice
                        AfterReturningAdviceInterceptor afterAdviceInterceptor = new AfterReturningAdviceInterceptor(aspectMethods.get(aopConfig.getAspectAfter()), aspectClass.newInstance());
                        advices.add(afterAdviceInterceptor);
                    }
                    //创建异常拦截器
                    if (!(null ==  aopConfig.getAspectAfterThrow() || "".equals(aopConfig.getAspectAfterThrow()))) {
                        //创建一个advice
                        AfterThrowingAdviceInterceptor afterThrowingAdviceInterceptor = new AfterThrowingAdviceInterceptor(aspectMethods.get(aopConfig.getAspectAfterThrow()), aspectClass.newInstance());
                        afterThrowingAdviceInterceptor.setThrowName(aopConfig.getAspectAfterThrowingName());
                        advices.add(afterThrowingAdviceInterceptor);
                    }

                    //保存被代理方法和执行器链对应关系
                    methodCache.put(method, advices);
                }
            }
        } catch (Exception e) {
            System.out.println("创建执行器链失败");
        }
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

}
