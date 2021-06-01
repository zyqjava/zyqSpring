package com.zyqSpring.boot.annotation;

import java.lang.annotation.*;

/**
 * Created by Enzo Cotter on 2021/6/1.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Configuration
public @interface ZyqSpringApplication {
    String[] scanBasePackages() default {};
    Class<?>[] scanBasePackageClasses() default {};
}
