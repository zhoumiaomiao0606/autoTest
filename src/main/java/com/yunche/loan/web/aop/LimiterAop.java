package com.yunche.loan.web.aop;

import com.genxiaogu.ratelimiter.service.impl.DistributedLimiter;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
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

        String route = "";
        int limit = 1;

        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        for (Annotation annotation : method.getAnnotations()) {
            /*
             * 如果方法具有Limiter注解，则需要把method，limit拿出来
             */
            if (annotation instanceof Limiter) {
                Limiter limiter = method.getAnnotation(Limiter.class);
                limit = limiter.limit();
                route = limiter.route();

                // 登录用户
                String obj = SessionUtils.getLoginUser().getId().toString();

                if (!distributedLimiter.execute(route, limit, obj)) {
                    throw new BizException("访问太过频繁，请稍后再试！");
                }
            }
        }

        return point.proceed();
    }
}
