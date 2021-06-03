package com.zyqSpring.boot.web;

import javax.servlet.ServletContext;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Enzo Cotter on 2021/6/2.
 */
public interface WebApplicationInitializer {

    void onStartup(ServletContext servletContext) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException;
}
