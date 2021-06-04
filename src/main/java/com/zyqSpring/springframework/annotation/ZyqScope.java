package com.zyqSpring.springframework.annotation;

import java.lang.annotation.*;

/**
 * Created by Enzo Cotter on 2021/6/4.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZyqScope {
    String value() default "singleton";
}
