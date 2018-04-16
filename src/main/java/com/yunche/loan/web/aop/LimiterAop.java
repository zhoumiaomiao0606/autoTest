package com.yunche.loan.web.aop;

import com.alibaba.fastjson.JSON;
import com.genxiaogu.ratelimiter.service.impl.DistributedLimiter;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.SessionUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


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

        String route = "";
        int limit = 1;

        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        /**
         * 如果方法具有Limiter注解，则需要把method，limit拿出来
         */
        Limiter limiter = method.getAnnotation(Limiter.class);
        limit = limiter.limit();
        route = limiter.route();

        // 唯一：登录用户 + 参数
        Parameter[] parameters = method.getParameters();
        String obj = SessionUtils.getLoginUser().getId().toString() + ":" + JSON.toJSONString(parameters);

        if (!distributedLimiter.execute(route, limit, obj)) {
            throw new BizException("访问太过频繁，请稍后再试！");
        }

        return point.proceed();
    }
}
