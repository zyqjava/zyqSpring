package org.zyqSpring.boot.annotation;

import org.zyqSpring.springframework.annotation.ZyqComponent;

import java.lang.annotation.*;

/**
 * Created by Enzo Cotter on 2021/6/11.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ZyqComponent
public @interface ClpRestController {
}
