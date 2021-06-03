package com.zyqSpring.boot.web;

import com.zyqSpring.springframework.Listener.ContextLoaderListener;
import com.zyqSpring.springframework.Listener.SpringListener;
import com.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;

import javax.servlet.ServletContext;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Enzo Cotter on 2021/6/3.
 */
public abstract class AbstractDispatcherServletInitializer implements WebApplicationInitializer{

    @Override
    public void onStartup(ServletContext servletContext) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        registerDispatcherServlet(servletContext);
    }


    protected void registerDispatcherServlet(ServletContext servletContext) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //创建spring的根应用上下文
        ZyqAnnotationApplicationContext rootAppContext = createRootApplicationContext();
        if (rootAppContext != null) {
            ContextLoaderListener listener = new ContextLoaderListener(rootAppContext);
            servletContext.addListener(listener);
            servletContext.setAttribute(ZyqAnnotationApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, rootAppContext);
        }
        //通知容器启动完成
        SpringListener.publish(rootAppContext, ContextLoaderListener.class);
    }

    protected abstract ZyqAnnotationApplicationContext createRootApplicationContext();

}
