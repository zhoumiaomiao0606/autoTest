package com.yunche.loan.web.aop;

import com.alibaba.fastjson.JSON;
import com.genxiaogu.ratelimiter.service.impl.DistributedLimiter;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.exception.BizException;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class LimiterAop {

    @Autowired
    private DistributedLimiter distributedLimiter;

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

        // 唯一：登录用户SessionId + 参数
        String obj = SecurityUtils.getSubject().getSession().getId().toString() + ":" + JSON.toJSONString(point.getArgs());

        if (!distributedLimiter.execute(route, limit, obj)) {
            throw new BizException("操作太过频繁，请勿重复点击！");
        }

        return point.proceed();
    }
}
