package com.zyqSpring.boot.web;

import javax.servlet.ServletContext;

/**
 * Created by Enzo Cotter on 2021/6/2.
 */
public interface WebApplicationInitializer {

    void onStartup(ServletContext servletContext) throws Exception;
}
