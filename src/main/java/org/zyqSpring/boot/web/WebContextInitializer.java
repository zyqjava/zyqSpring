package org.zyqSpring.boot.web;

import org.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;

import javax.servlet.ServletContext;


/**
 * Created by Enzo Cotter on 2021/6/2.
 */
public abstract class WebContextInitializer extends AbstractDispatcherServletInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws Exception {
        createRootApplicationContext(servletContext);
    }

    @Override
    protected ZyqAnnotationApplicationContext createRootApplicationContext(ServletContext servletContext) throws Exception {
        Class<?> configClasses = getRootConfigClasses(servletContext);
        //创建spring的根应用上下文
        ZyqAnnotationApplicationContext rootAppContext = new ZyqAnnotationApplicationContext(configClasses);
        return rootAppContext;
    }

    protected abstract Class<?> getRootConfigClasses(ServletContext servletContext) throws Exception;

}
