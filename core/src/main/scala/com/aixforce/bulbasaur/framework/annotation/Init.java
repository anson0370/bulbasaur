package com.aixforce.bulbasaur.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     This annotation is used on a method that need execute after DI,
 *     just like @PostConstruct. Also you can use @PostConstruct instead.
 * </p>
 *
 * Created by IntelliJ IDEA.
 * User: guichen - anson
 * Date: 12-6-11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Init {
}
