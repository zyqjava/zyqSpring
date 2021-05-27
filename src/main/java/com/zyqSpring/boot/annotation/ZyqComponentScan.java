package com.zyqSpring.boot.annotation;

import java.lang.annotation.*;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ZyqComponentScan {
    String[] value() default {};
}

