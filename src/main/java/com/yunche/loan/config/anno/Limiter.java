package com.yunche.loan.config.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liuzhe
 * @date 2018/4/16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Limiter {

    /**
     * 限流route
     *
     * @return
     */
    String route();

    /**
     * 限流次数
     *
     * @return
     */
    int limit() default 1;
}
