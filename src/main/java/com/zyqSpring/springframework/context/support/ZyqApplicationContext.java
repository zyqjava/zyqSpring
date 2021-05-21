package com.zyqSpring.springframework.context.support;

import com.zyqSpring.springframework.beans.support.ZyqBeanDefinitionReader;
import com.zyqSpring.springframework.context.ApplicationContext;

/**
 * Created by Enzo Cotter on 2021/5/21.
 */
public class ZyqApplicationContext implements ApplicationContext {

    //配置文件的路径
    private String configLocation;

    public ZyqApplicationContext(String configLocation) {
        this.configLocation = configLocation;
        try {

        } catch (Exception e) {
            System.out.println("容器启动失败");
        }
    }


    private void refresh() throws Exception {
        //step1:定位，定位配置文件
        ZyqBeanDefinitionReader zyqBeanDefinitionReader = new ZyqBeanDefinitionReader(this.configLocation);
        //step2:加载配置文件，扫描相关的类，把他们封装成BeanDefinition
        //step3:注册，把配置信息放到容器里面（ioc容器）
        //把不是延时加载的类，提前初始化
    }
    @Override
    public Object getBean(String name) throws Exception {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws Exception {
        return (T) getBean(requiredType.getName());
    }
}
