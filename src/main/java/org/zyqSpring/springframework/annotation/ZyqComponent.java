package org.zyqSpring.springframework.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZyqComponent {
    String value() default "";
}
