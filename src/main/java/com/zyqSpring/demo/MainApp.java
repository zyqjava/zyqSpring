package com.zyqSpring.demo;

import com.zyqSpring.boot.SpringApplication;
import com.zyqSpring.boot.annotation.ZyqSpringApplication;

/**
 * Created by Enzo Cotter on 2021/5/24.
 */
@ZyqSpringApplication
public class MainApp {
    public static void main(String[] args) throws Exception{
        SpringApplication.run(MainApp.class, args);
    }
}
