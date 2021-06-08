package org.zyqSpring.springframework.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZyqRequestMapping {
    String value() default "";
}
