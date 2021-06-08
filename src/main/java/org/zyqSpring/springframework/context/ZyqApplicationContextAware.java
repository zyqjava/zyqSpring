package org.zyqSpring.springframework.context;

import org.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;


/**
 * Created by Enzo Cotter on 2021/5/21.
 */
public interface ZyqApplicationContextAware {
    void setApplicationContext(ZyqAnnotationApplicationContext zyqAnnotationApplicationContext);
}
