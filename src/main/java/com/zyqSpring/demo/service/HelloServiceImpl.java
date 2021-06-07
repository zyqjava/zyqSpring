package com.zyqSpring.demo.service;

import com.zyqSpring.springframework.annotation.ZyqAutowired;
import com.zyqSpring.springframework.annotation.ZyqService;
import com.zyqSpring.springframework.context.ZyqBeanNameAware;

/**
 * Created by Enzo Cotter on 2021/5/24.
 */
@ZyqService
public class HelloServiceImpl implements HelloService, ZyqBeanNameAware {

    @ZyqAutowired
    private MotherService motherService;

    private String beanName;

    @Override
    public void sayHello() {
        System.out.println("hello!world + --" + beanName);
        motherService.call();
    }

    @Override
    public void setZyqBeanName(String name) {
        this.beanName = name;
    }
}
