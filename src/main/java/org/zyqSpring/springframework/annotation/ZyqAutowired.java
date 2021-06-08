package org.zyqSpring.springframework.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZyqAutowired {
    String value() default "";
}
