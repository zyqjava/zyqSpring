package org.zyqSpring.springframework.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ZyqComponent
public @interface ZyqController {
    String value() default "";
}
