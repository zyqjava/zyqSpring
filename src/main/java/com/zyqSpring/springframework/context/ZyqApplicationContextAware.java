package com.zyqSpring.springframework.context;

import com.zyqSpring.springframework.context.support.ZyqApplicationContext;

/**
 * Created by Enzo Cotter on 2021/5/21.
 */
public interface ZyqApplicationContextAware {
    void setApplicationContext(ZyqApplicationContext applicationContext);
}
