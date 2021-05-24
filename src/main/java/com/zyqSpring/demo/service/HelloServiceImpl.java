package com.zyqSpring.demo.service;

import com.zyqSpring.springframework.annotation.ZyqAutowired;
import com.zyqSpring.springframework.annotation.ZyqService;

/**
 * Created by Enzo Cotter on 2021/5/24.
 */
@ZyqService
public class HelloServiceImpl implements HelloService{

    @ZyqAutowired
    private MotherService motherService;

    @Override
    public void sayHello() {
        System.out.println("hello!world");
        motherService.call();
    }
}
