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
    String value();

    /**
     * 限流次数，配合expire使用      默认：1次/s
     *
     * @return
     */
    int limit() default 1;

    /**
     * 限流时间跨度 （单位：秒）      默认：1s
     *
     * @return
     */
    int expire() default 1;
}
