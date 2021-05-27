package com.zyqSpring.boot;

import com.zyqSpring.mvc.support.Protocol;
import com.zyqSpring.mvc.support.ProtocolFactory;
import com.zyqSpring.springframework.context.ApplicationContext;
import com.zyqSpring.springframework.context.support.ZyqApplicationContext;
import org.apache.catalina.LifecycleException;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
public class SpringApplication {
    public static  void run() throws LifecycleException, InterruptedException {

        //初始化spring
        ApplicationContext applicationContext = new ZyqApplicationContext("application.properties");

        //启动Tomcat/netty
        Protocol protocol = ProtocolFactory.getProtocol();
        protocol.start();
    }
}
