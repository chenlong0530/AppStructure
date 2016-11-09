package com.app.library.net.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenlong on 16/10/18.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParameter {
    enum METHOD {POST, GET}

    METHOD method() default METHOD.POST;

    String name() default "";
}
