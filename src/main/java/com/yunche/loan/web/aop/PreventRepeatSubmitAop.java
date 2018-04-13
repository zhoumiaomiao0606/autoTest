package com.yunche.loan.web.aop;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.IPUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        EmployeeDO employeeDO = SessionUtils.getLoginUser();

        String value = (String) redisTemplate.opsForValue().get(employeeDO.getId());
        if (StringUtils.isNotBlank(value)){
            throw new BizException("操作过于频繁,请勿重复提交");
        }
        redisTemplate.opsForValue().set(employeeDO.getId(),"loginUserId",1000,TimeUnit.MILLISECONDS);
        Object object = point.proceed();

        return object;
    }
}
