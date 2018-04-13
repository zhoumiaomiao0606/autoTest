package com.yunche.loan.web.aop;

import com.yunche.loan.config.anno.PreventRepeatSubmit;
import com.yunche.loan.config.exception.BizException;

import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Component
public class PreventRepeatSubmitAop {


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @param point
     */
    @Around("@annotation(com.yunche.loan.config.anno.PreventRepeatSubmit)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();

        Method method = methodSignature.getMethod();

        PreventRepeatSubmit preventRepeatSubmit  = method.getAnnotation(PreventRepeatSubmit.class);
        String methodKey = preventRepeatSubmit.value();
        if(StringUtils.isBlank(methodKey)){
            throw new BizException("切点 method key 为空");
        }
        EmployeeDO employeeDO = SessionUtils.getLoginUser();
        Long loginUserId =  employeeDO.getId();
        String key = new StringBuffer(methodKey).append("-").append(String.valueOf(loginUserId)).toString();
        String value = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(value)){
            throw new BizException("操作过于频繁,请勿重复提交");
        }
        redisTemplate.opsForValue().set(key,"锁占用中");

        try{
            return point.proceed();
        }finally {
             redisTemplate.delete(key);
        }

    }
}
