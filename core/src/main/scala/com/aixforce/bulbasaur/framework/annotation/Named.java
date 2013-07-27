package com.aixforce.bulbasaur.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This annotation is used on a field or a constructor method's parameter that
 * will inject a bean bound by given name
 * </p>
 * <p/>
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-6-11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Named {
    public String value() default "";
}
