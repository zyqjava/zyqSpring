package com.zyqSpring.boot.web;

import com.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;


/**
 * Created by Enzo Cotter on 2021/6/2.
 */
public abstract class WebContextInitializer extends AbstractDispatcherServletInitializer {

    @Override
    protected ZyqAnnotationApplicationContext createRootApplicationContext() {
        Class<?> configClasses = getRootConfigClasses();
        //创建spring的根应用上下文
        ZyqAnnotationApplicationContext rootAppContext = new ZyqAnnotationApplicationContext(configClasses);
        return rootAppContext;
    }

    protected abstract Class<?> getRootConfigClasses();

}
