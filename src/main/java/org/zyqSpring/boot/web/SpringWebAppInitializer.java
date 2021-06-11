package org.zyqSpring.boot.web;

import org.zyqSpring.springframework.utils.FilesParseUtils;

import javax.servlet.ServletContext;

/**
 * Created by Enzo Cotter on 2021/6/3.
 */
public class SpringWebAppInitializer extends WebContextInitializer {

    public Class<?> getMainClass(ServletContext servletContext) throws Exception {
        String contextPath = servletContext.getRealPath("/");
        Class<?> mainClass = new FilesParseUtils().getApplicationClass(contextPath);
        if (mainClass == null) {
            throw new Exception("扫描整个项目都没找到启动类。我也是醉了");
        }
        return mainClass;
    }

    //加载根配置信息 spring核心
    protected Class<?> getRootConfigClasses(ServletContext servletContext) throws Exception {
        return getMainClass(servletContext);
    }
}
