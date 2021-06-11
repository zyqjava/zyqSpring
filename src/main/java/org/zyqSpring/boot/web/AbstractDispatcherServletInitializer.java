package org.zyqSpring.boot.web;

import org.zyqSpring.mvc.servlet.DispatcherServlet;
import org.zyqSpring.springframework.Listener.ContextLoaderListener;
import org.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * Created by Enzo Cotter on 2021/6/3.
 */
public abstract class AbstractDispatcherServletInitializer implements WebApplicationInitializer{

    @Override
    public void onStartup(ServletContext servletContext) throws Exception {
        registerDispatcherServlet(servletContext);
    }


    protected void registerDispatcherServlet(ServletContext servletContext) throws Exception {
        //创建spring的根应用上下文
        ZyqAnnotationApplicationContext rootAppContext = createRootApplicationContext(servletContext);
        if (rootAppContext != null) {
            ContextLoaderListener listener = new ContextLoaderListener(rootAppContext);
            servletContext.addListener(listener);
            servletContext.setAttribute(ZyqAnnotationApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, rootAppContext);
        }
        //通知容器启动完成
        //SpringListener.publish(rootAppContext, ContextLoaderListener.class);
        DispatcherServlet servlet = new DispatcherServlet();
        ServletRegistration.Dynamic app = servletContext.addServlet("app", servlet);
        app.addMapping("/");
        app.setLoadOnStartup(1);

    }

    protected abstract ZyqAnnotationApplicationContext createRootApplicationContext(ServletContext servletContext) throws Exception;

}
