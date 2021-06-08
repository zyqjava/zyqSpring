package org.zyqSpring.springframework.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ZyqComponent
@Documented
public @interface ZyqService {
    String value() default "";
}
