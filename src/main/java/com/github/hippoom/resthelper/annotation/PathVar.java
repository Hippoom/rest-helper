package com.github.hippoom.resthelper.annotation;


import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVar {

    /**
     * The URI template variable to bind to.
     */
    String value() default "";

}

