package com.zyqSpring;

import com.zyqSpring.springframework.context.support.ZyqApplicationContext;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        ZyqApplicationContext zyqApplicationContext = new ZyqApplicationContext("com.zyqSpring.springframework");
    }

}

