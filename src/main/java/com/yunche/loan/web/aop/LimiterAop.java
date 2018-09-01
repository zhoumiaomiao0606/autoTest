package com.yunche.loan.web.aop;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.exception.BizException;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Aspect
@Component
public class LimiterAop {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * @param point
     */
    @Around("@annotation(com.yunche.loan.config.anno.Limiter)")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        /**
         * 如果方法具有Limiter注解，则需要把method，limit拿出来
         */
        Limiter limiter = method.getAnnotation(Limiter.class);
        String route = limiter.value();
        int limit = limiter.limit();
        int expire = limiter.expire();

        // 唯一：登录用户SessionId + 参数
        String obj = SecurityUtils.getSubject().getSession().getId().toString() + ":" + JSON.toJSONString(point.getArgs());

        if (!doLimiter(route, limit, obj, expire)) {
            throw new BizException("操作太过频繁，请勿重复点击！");
        }

        return point.proceed();
    }

    /**
     * 限流Redis-lua实现
     *
     * @param route
     * @param limit
     * @param obj
     * @param expire
     * @return
     */
    private boolean doLimiter(String route, Integer limit, String obj, int expire) {

        final String key = route.concat(obj);

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/rateLimit.lua")));
        redisScript.setResultType(Long.class);

        Object result = stringRedisTemplate.execute(redisScript, Lists.newArrayList(key),
                String.valueOf(limit), String.valueOf(expire));

        if ((long) result == 1) {
            return true;
        }
        return false;
    }

}
