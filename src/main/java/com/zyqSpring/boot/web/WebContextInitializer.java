package com.zyqSpring.boot.web;

import com.zyqSpring.boot.annotation.ZyqSpringApplication;
import com.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;


/**
 * Created by Enzo Cotter on 2021/6/2.
 */
public abstract class WebContextInitializer extends AbstractDispatcherServletInitializer {

    private Class<?> configClasses;

    @Override
    public void onStartup(ServletContext servletContext) throws Exception {
        String contextPath = servletContext.getRealPath("/");
        Class<?> mainClass = getScanClass(contextPath);
        if (mainClass == null) {
            throw new Exception("扫描整个项目都没找到启动类。我也是醉了");
        }
        this.configClasses = mainClass;
    }

    protected Class<?> getScanClass(String contextPath) throws ClassNotFoundException {
        File files = new File(contextPath);
        String scanPackage = files.getName();
        for (File file : files.listFiles()) {
            if (!file.isDirectory()) {
                if (file.getName().endsWith(".class")) {
                    scanPackage = scanPackage + "." + file.getName();
                    Class<?> aClass = Class.forName(scanPackage);
                    if (aClass.isAnnotationPresent(ZyqSpringApplication.class)) {
                        return aClass;
                    }
                } else {
                    getScanClass(contextPath + "/" + file.getName());
                }
            }
        }
        return null;
    }

    @Override
    protected ZyqAnnotationApplicationContext createRootApplicationContext() {
        //创建spring的根应用上下文
        ZyqAnnotationApplicationContext rootAppContext = new ZyqAnnotationApplicationContext(this.configClasses);
        return rootAppContext;
    }

    protected abstract Class<?> getRootConfigClasses();

}
