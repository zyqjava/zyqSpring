package com.zyq.demo.controller;

import org.zyqSpring.mvc.servlet.ModelAndView;
import org.zyqSpring.springframework.annotation.ZyqController;
import org.zyqSpring.springframework.annotation.ZyqRequestMapping;

import java.util.HashMap;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
@ZyqController
public class HelloController {

    @ZyqRequestMapping("/hello")
    public ModelAndView hello() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("data1", "hello");
        model.put("data2", "world");
        return new ModelAndView("test", model);
    }

}

