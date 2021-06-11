package org.zyqSpring.boot.web;

import lombok.SneakyThrows;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Enzo Cotter on 2021/6/2.
 */
@HandlesTypes(WebApplicationInitializer.class)
public class TomcatServletContainerInitializer implements ServletContainerInitializer {


    /**
     * 遵循规范的容器启动时会调用此方法
     *      * @param arg0 感兴趣的类型的所有子类型
     *      * @param sc 当前web在容器的上下文对象
     *      * @throws ServletException
     */
    @SneakyThrows
    @Override
    public void onStartup(Set<Class<?>> webAppInitializerClasses, ServletContext servletContext) throws ServletException {


        Iterator var4;
        if (webAppInitializerClasses !=null){
            var4 = webAppInitializerClasses.iterator();
            while(var4.hasNext()){
                Class<?> clazz= (Class<?>) var4.next();
                if (!clazz.isInterface()&& ! Modifier.isAbstract(clazz.getModifiers()) && WebApplicationInitializer.class.isAssignableFrom(clazz)){
                    try {
                        ((WebApplicationInitializer) clazz.newInstance()).onStartup(servletContext);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        //存储WebApplicationInitializer实例。在本项目中有两个实例:WebContextInitializer.java和SecurityWebApplicationInitializer.java
        /*List<WebApplicationInitializer> initializers = new LinkedList<WebApplicationInitializer>();

        if (webAppInitializerClasses != null) {
            for (Class<?> waiClass : webAppInitializerClasses) {
                Constructor ctor = waiClass.getDeclaredConstructor(ServletContext.class);
                ctor.setAccessible(true);
                initializers.add((WebContextInitializer) ctor.newInstance());
            }
        }


        //遍历WebApplicationInitializer实例，依次调用实例的onStartup方法。
        for (WebApplicationInitializer initializer : initializers) {
            initializer.onStartup(servletContext);
        }*/

        //存储WebApplicationInitializer实例。在本项目中有两个实例:WebContextInitializer.java和SecurityWebApplicationInitializer.java
        //WebApplicationInitializer.class.newInstance().onStartup(servletContext);

    }
}
