package org.zyqSpring.boot;

import org.zyqSpring.mvc.support.Protocol;
import org.zyqSpring.mvc.support.ProtocolFactory;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
public class SpringApplication {

    public static void run(Class<?> clazz, String[] args) throws Exception {

        //SpringContextInitializer.initApplicationContext(clazz, args);

        //启动Tomcat/netty
        Protocol protocol = ProtocolFactory.getProtocol();
        protocol.start();


    }
}
