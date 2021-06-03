package com.zyqSpring.springframework.Listener;

import com.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Enzo Cotter on 2021/6/2.
 */
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

    public ContextLoaderListener() {

    }

    public ContextLoaderListener(ZyqAnnotationApplicationContext context) {
        super(context);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        initWebApplicationContext(event.getServletContext());
    }

    public void contextDestroyed(ServletContextEvent event) {
    }
}
