package org.zyqSpring.boot.web;

import org.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;
import org.zyqSpring.springframework.utils.FilesParseUtils;

import javax.servlet.ServletContext;


/**
 * Created by Enzo Cotter on 2021/6/2.
 */
public abstract class WebContextInitializer extends AbstractDispatcherServletInitializer {

    private Class<?> configClasses;

    private String basePackage = null;

    @Override
    public void onStartup(ServletContext servletContext) throws Exception {
        String contextPath = servletContext.getRealPath("/");
        Class<?> mainClass = new FilesParseUtils().getApplicationClass(contextPath);
        if (mainClass == null) {
            throw new Exception("扫描整个项目都没找到启动类。我也是醉了");
        }
        this.configClasses = mainClass;
    }


    @Override
    protected ZyqAnnotationApplicationContext createRootApplicationContext() {
        //创建spring的根应用上下文
        ZyqAnnotationApplicationContext rootAppContext = new ZyqAnnotationApplicationContext(this.configClasses);
        return rootAppContext;
    }


}
