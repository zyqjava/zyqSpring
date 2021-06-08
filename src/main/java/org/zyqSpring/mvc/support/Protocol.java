package org.zyqSpring.mvc.support;

import org.apache.catalina.LifecycleException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface Protocol {
    void start() throws InterruptedException, LifecycleException;
    String send() throws InterruptedException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
