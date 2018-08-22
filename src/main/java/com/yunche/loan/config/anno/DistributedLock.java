package com.yunche.loan.config.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liuzhe
 * @date 2018/8/3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLock {

    /**
     * 当前锁的：自动过期时间  （单位：微秒）默认值：1000
     *
     * @return
     */
    long value() default 1000;
}
