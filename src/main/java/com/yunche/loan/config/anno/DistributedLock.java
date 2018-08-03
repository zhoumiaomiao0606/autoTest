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
     * 当前锁的：key
     * <p>
     * 默认取：注解标注的-当前方法名
     *
     * @return
     */
    String key() default "lock:key";

    /**
     * 当前锁的：随机val
     * <p>
     * 默认取：注解标注的-当前方法名
     *
     * @return
     */
    String val() default "lock:val:random";

    /**
     * 当前锁的：过期时间  （单位：微秒）默认值：1000
     *
     * @return
     */
    int timeOut() default 1000;
}
