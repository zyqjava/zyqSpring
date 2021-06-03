package com.zyqSpring.springframework.Listener;

import com.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;

import javax.servlet.ServletContext;

/**
 * Created by Enzo Cotter on 2021/6/2.
 */
public class ContextLoader {

    private ZyqAnnotationApplicationContext context;

    public ContextLoader() {}

    public ContextLoader(ZyqAnnotationApplicationContext context) {
        this.context = context;
    }


    public ZyqAnnotationApplicationContext initWebApplicationContext(ServletContext servletContext) {
        if (this.context == null) {
            this.context = createWebApplicationContext(servletContext);
        }
        return this.context;
    }


    protected ZyqAnnotationApplicationContext createWebApplicationContext(ServletContext servletContext) {
        servletContext.setAttribute(ZyqAnnotationApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
        return this.context;
    }

}
