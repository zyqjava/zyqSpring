package com.zyqSpring.demo;

import com.zyqSpring.demo.service.HelloService;
import com.zyqSpring.springframework.context.ApplicationContext;
import com.zyqSpring.springframework.context.support.ZyqApplicationContext;

/**
 * Created by Enzo Cotter on 2021/5/24.
 */
public class MainApp {
    public static void main(String[] args) throws Exception{
        ApplicationContext applicationContext = new ZyqApplicationContext("application.properties");
        HelloService helloService = (HelloService) applicationContext.getBean("helloService");
        helloService.sayHello();
    }
}
