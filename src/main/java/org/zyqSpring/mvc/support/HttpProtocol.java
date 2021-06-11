package org.zyqSpring.mvc.support;

import org.apache.catalina.LifecycleException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class HttpProtocol implements Protocol {

    @Override
    public void start() throws LifecycleException {
        HttpServer httpServer = new HttpServer();
        httpServer.start("127.0.0.1", 9090);
    }

    @Override
    public String send() throws InterruptedException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return null;
    }
}
