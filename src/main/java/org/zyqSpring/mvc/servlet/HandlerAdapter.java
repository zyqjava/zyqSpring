package org.zyqSpring.mvc.servlet;

import lombok.Data;
import org.zyqSpring.springframework.annotation.ZyqRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 简单讲就是负责接收用户的请求，
 * 然后将参数填充到Controller中的方法中调用。
 */
@Data
public class HandlerAdapter {

    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMapping handlerMapping = (HandlerMapping) handler;

        //把方法的形参列表和request的参数列表所在顺序进行一一对应
        Map<String, Integer> paramIndexMapping = new HashMap<>();

        //提取方法中加了注解的参数
        //拿到方法上的注解，得到一个二维数组
        //因为参数前可以添加多个注解,,一个参数上不可以添加相同的注解,同一个注解可以加在不同的参数上
        Annotation[][] parameterAnnotations = handlerMapping.getMethod().getParameterAnnotations();
        //遍历获取每个参数
        for (int i = 0; i < parameterAnnotations.length; i++) {
            //遍历获取参数上的注解
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof ZyqRequestParam) {
                    String paramName  = ((ZyqRequestParam) annotation).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        //提取方法中的request和response参数
        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class) {
                paramIndexMapping.put(parameterType.getName(), i);
            }
        }

        //获得方法的形参
        Map<String, String[]> parameterMap = request.getParameterMap();
        //对应controller的方法的实参
        Object[] paramValues = new Object[parameterTypes.length];

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");

            if (!paramIndexMapping.containsKey(entry.getKey())) {
                continue;
            }

            Integer index = paramIndexMapping.get(entry.getKey());
            paramValues[index] = parseStringValue(value, parameterTypes[index]);
        }

        //填充HttpServletRequest参数
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            Integer index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = request;
        }

        //填充HttpServletResponse参数
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }

        //反射调用controller的方法
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (null == result || result instanceof Void) {
            return null;
        }

        //解析controller的方法返回
        Class<?> returnType = handlerMapping.getMethod().getReturnType();
        boolean isModelAndView = returnType == ModelAndView.class;
        if (isModelAndView) {
            return (ModelAndView) result;
        } else if(returnType == Void.class) {
            return null;
        } else if (returnType == String.class) {

        }
        return null;
    }

    /**
     * request中接收的参数都是string类型的，需要转换为controller中实际的参数类型
     * 暂时只支持string、int、double类型
     */
    private Object parseStringValue(String value, Class<?> parameterType) {
        if (String.class == parameterType) {
            return value;
        }
        if (Integer.class == parameterType) {
            return Integer.valueOf(value);
        } else if (Double.class == parameterType) {
            return Double.valueOf(value);
        } else {
            if (value != null) {
                return value;
            }
            return null;
        }
        //还有，继续加if
        //其他类型在这里暂时不实现，希望小伙伴自己来实现
    }
}
