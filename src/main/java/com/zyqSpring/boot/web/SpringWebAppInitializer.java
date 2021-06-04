package com.zyqSpring.boot.web;

import com.zyqSpring.demo.MainApp;

/**
 * Created by Enzo Cotter on 2021/6/3.
 */
public class SpringWebAppInitializer extends WebContextInitializer {

    //加载根配置信息 spring核心
    protected Class<?> getRootConfigClasses() {
        return MainApp.class;
    }
}
