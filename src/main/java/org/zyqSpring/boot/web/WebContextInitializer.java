package org.zyqSpring.boot.web;

import org.zyqSpring.boot.annotation.ZyqSpringApplication;
import org.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;


/**
 * Created by Enzo Cotter on 2021/6/2.
 */
public abstract class WebContextInitializer extends AbstractDispatcherServletInitializer {

    private Class<?> configClasses;

    private String basePackage = null;

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
                if (file.getName().endsWith(".class") || file.getName().endsWith(".java")) {
                    if (file.getName().endsWith(".java")) {
                        String realClass = file.getName().replace(".java", ".class");
                        basePackage = basePackage + "." + realClass;
                    } else {
                        basePackage = basePackage + "." + file.getName();
                    }

                    Class<?> aClass = Class.forName(scanPackage);
                    if (aClass.isAnnotationPresent(ZyqSpringApplication.class)) {
                        return aClass;
                    }
                }
            } else {
                if ("org".equals(file.getName())) {
                    break;
                }
                if ("java".equals(file.getParentFile().getName())) {
                    basePackage = file.getName();
                }
                if (basePackage != null) {
                    System.out.println(file.getParentFile().getName());
                    System.out.println(basePackage.contains(file.getParentFile().getName()));
                    if (basePackage.contains(file.getParentFile().getName())) {
                        basePackage = basePackage + "." + file.getName();
                    }
                }

                getScanClass(contextPath + "\\" + file.getName());
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
