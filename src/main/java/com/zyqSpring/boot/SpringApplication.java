package com.zyqSpring.boot;

import com.zyqSpring.boot.web.SpringContextInitializer;
import com.zyqSpring.mvc.support.Protocol;
import com.zyqSpring.mvc.support.ProtocolFactory;
import org.apache.catalina.LifecycleException;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
public class SpringApplication {

    public static  void run(Class<?> clazz, String[] args) throws LifecycleException, InterruptedException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        //启动Tomcat/netty
        Protocol protocol = ProtocolFactory.getProtocol();
        protocol.start();

        SpringContextInitializer.initApplicationContext(clazz, args);
    }
}
