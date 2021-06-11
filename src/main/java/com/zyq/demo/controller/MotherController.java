package com.zyq.demo.controller;

import org.zyqSpring.mvc.servlet.ModelAndView;
import org.zyqSpring.springframework.annotation.ZyqController;
import org.zyqSpring.springframework.annotation.ZyqRequestMapping;

import java.util.HashMap;

/**
 * Created by Enzo Cotter on 2021/6/11.
 */
@ZyqController
@ZyqRequestMapping("/mother")
public class MotherController {

    @ZyqRequestMapping("/mother")
    public ModelAndView mother() {
        HashMap<String, Object> model = new HashMap<>();
        model.put("data1", "hello");
        model.put("data2", "mother");
        return new ModelAndView("test", model);
    }
}
