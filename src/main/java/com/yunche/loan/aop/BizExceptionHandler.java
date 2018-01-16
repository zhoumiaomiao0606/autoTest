package com.yunche.loan.aop;

import com.yunche.loan.exception.BizException;
import com.yunche.loan.result.ResultBean;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;

/**
 * @author liuzhe
 * @date 2018/1/10
 */
@Aspect
@Component
public class BizExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BizExceptionHandler.class);

    /**
     * 捕获所有controller层的方法
     */
    @Pointcut("execution(* com.yunche.loan.controller.*.*(..))")
    public void controller() {
    }

    @Around("controller()")
    public Object doBefore(ProceedingJoinPoint pjp) {

        try {
            return pjp.proceed();
        } catch (Throwable throwable) {
            logger.error("BizExceptionHandler : ", throwable);
            if (throwable instanceof BizException) {
                return ResultBean.ofError(throwable.getMessage());
            } else if (throwable instanceof IllegalArgumentException) {
                return ResultBean.ofError(throwable.getMessage());
            } else if (throwable instanceof NullPointerException) {
                return ResultBean.ofError(throwable.getMessage());
            } else if (throwable instanceof BadSqlGrammarException) {
                return ResultBean.ofError("糟糕，出错啦！");
            } else if (throwable instanceof RuntimeException) {
                return ResultBean.ofError("糟糕，出错啦！");
            } else {
                String errorMsg = throwable.toString() == null ? throwable.getMessage() : throwable.toString();
                return ResultBean.ofError(errorMsg == null || errorMsg.equals("") ? "未知错误" : throwable.toString());
            }
        }
    }
}
