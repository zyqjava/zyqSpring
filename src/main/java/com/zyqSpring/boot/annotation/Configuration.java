package com.zyqSpring.boot.annotation;

import com.zyqSpring.springframework.annotation.ZyqComponent;

import java.lang.annotation.*;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ZyqComponent
public @interface Configuration {
    String value() default "";
}
