package org.zyqSpring.mvc.servlet;

import java.util.Map;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
public class ModelAndView {

    //模板名字
    private String viewName;

    //模板中填充的参数
    private Map<String, ?> model;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

}
