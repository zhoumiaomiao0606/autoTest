package com.yunche.loan.web.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static com.yunche.loan.web.aspect.GlobalExceptionHandler.doGlobalExceptionHandler;

/**
 * @author liuzhe
 */
@Aspect
@Component
public class WebsocketGlobalExceptionHandler {


    @Around(value = "@annotation(org.springframework.messaging.handler.annotation.MessageMapping)")
    public Object exceptionHandler(ProceedingJoinPoint pjp) {

        try {

            Object result = pjp.proceed();

            return result;

        } catch (Throwable e) {

            return doGlobalExceptionHandler(e);
        }
    }

}
