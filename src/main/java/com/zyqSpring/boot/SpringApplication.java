package com.zyqSpring.boot;

import com.zyqSpring.boot.web.SpringContextInitializer;
import com.zyqSpring.mvc.support.Protocol;
import com.zyqSpring.mvc.support.ProtocolFactory;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
public class SpringApplication {

    public static  void run(Class<?> clazz, String[] args) throws Exception {

        SpringContextInitializer.initApplicationContext(clazz, args);

        //启动Tomcat/netty
        Protocol protocol = ProtocolFactory.getProtocol();
        protocol.start();


    }
}
