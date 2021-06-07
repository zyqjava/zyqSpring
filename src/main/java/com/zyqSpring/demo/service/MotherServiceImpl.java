package com.zyqSpring.demo.service;

import com.zyqSpring.springframework.annotation.ZyqService;

/**
 * Created by Enzo Cotter on 2021/5/24.
 */
@ZyqService
public class MotherServiceImpl implements MotherService{

    @Override
    public void call() {
        System.out.println("你妈妈喊你回家吃饭");
    }
}
