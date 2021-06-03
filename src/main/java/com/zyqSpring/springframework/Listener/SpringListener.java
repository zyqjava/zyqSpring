package com.zyqSpring.springframework.Listener;

import com.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Enzo Cotter on 2021/6/2.
 */
public class SpringListener {
    public static Object publish(ZyqAnnotationApplicationContext context, Class< ? > clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class[] paramTypes = {ZyqAnnotationApplicationContext.class};
        Object[] params = {context};
        Constructor<?> constructor = clazz.getConstructor(paramTypes);
        return constructor.newInstance(params);
    }
}
