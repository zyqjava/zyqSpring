package com.zyqSpring.springframework.beans.support;

import java.util.Properties;

/**
 * 我们需要读取配置文件，扫描相关的类才能解析成BeanDefinition，
 * 这个读取 + 扫描的类就是BeanDefinitionReader
 */
public class ZyqBeanDefinitionReader {

    //配置文件
    private Properties config = new Properties();

    //配置文件中指定需要扫描的包名
    private final String SCAN_PACKAGE = "scanPackage";

    public ZyqBeanDefinitionReader(String... locations) {
    }

    public Properties getConfig() {
        return config;
    }
}
