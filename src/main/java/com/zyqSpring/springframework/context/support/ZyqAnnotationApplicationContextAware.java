package com.zyqSpring.springframework.context.support;

import com.zyqSpring.springframework.context.ZyqApplicationContextAware;

/**
 * Created by Enzo Cotter on 2021/6/1.
 */
public class ZyqAnnotationApplicationContextAware implements ZyqApplicationContextAware {

    private ZyqAnnotationApplicationContext zyqAnnotationApplicationContext;

    @Override
    public void setApplicationContext(ZyqApplicationContext applicationContext) {

    }

    @Override
    public void setApplicationContext(ZyqAnnotationApplicationContext zyqAnnotationApplicationContext) {

    }

    @Override
    public ZyqAnnotationApplicationContext getAnnotationApplicationContext() {
        return this.zyqAnnotationApplicationContext;
    }

    @Override
    public ZyqApplicationContext getApplicationContext() {
        return null;
    }
}
