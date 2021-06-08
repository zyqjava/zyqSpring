package org.zyqSpring.springframework.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZyqRequestParam {
    String value() default "";
}
