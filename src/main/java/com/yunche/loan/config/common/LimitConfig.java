package com.yunche.loan.config.common;

import com.genxiaogu.ratelimiter.advice.MethodRateLimiterBeforeInterceptor;
import com.genxiaogu.ratelimiter.annotation.Limiter;
import com.genxiaogu.ratelimiter.common.LimiterException;
import com.genxiaogu.ratelimiter.service.impl.DistributedLimiter;
import com.yunche.loan.config.util.SessionUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author liuzhe
 * @date 2018/4/16
 */
//@Component
public class LimitConfig extends MethodRateLimiterBeforeInterceptor {

    Logger logger = LoggerFactory.getLogger(MethodRateLimiterBeforeInterceptor.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    DistributedLimiter distributedLimiter;

    /**
     * 执行逻辑
     *
     * @param methodInvocation
     * @return Object
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String route = "";
        int limit = 1;

        Method method = methodInvocation.getMethod();

        for (Annotation annotation : method.getAnnotations()) {
            /*
             * 如果方法具有Limiter注解，则需要把method，limit拿出来
             */
            if (annotation instanceof Limiter) {
                Limiter limiter = method.getAnnotation(Limiter.class);
                route = limiter.route();
                limit = limiter.limit();
                // 登录用户
                String obj = SessionUtils.getLoginUser().getId().toString();

                if (!distributedLimiter.execute(route, limit, obj)) {
                    throw new LimiterException("访问太过频繁，请稍后再试！");
                }
            }
        }
        return methodInvocation.proceed();
    }

}
