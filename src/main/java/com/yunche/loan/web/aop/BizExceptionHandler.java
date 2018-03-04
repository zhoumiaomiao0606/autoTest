package com.yunche.loan.web.aop;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

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
    @Pointcut("execution(* com.yunche.loan.web.controller.*.*(..))")
    public void controller() {
    }

    @Around("controller()")
    public Object doBefore(ProceedingJoinPoint pjp) {

        try {
            // log
//            log();
            return pjp.proceed();
        } catch (Throwable throwable) {
            logger.error("BizExceptionHandler : ", throwable);
            if (throwable instanceof BizException) {
                return ResultBean.ofError(throwable.getMessage());
            } else if (throwable instanceof IllegalArgumentException) {
                return ResultBean.ofError(throwable.getMessage());
            } else if (throwable instanceof NullPointerException) {
                return ResultBean.ofError(throwable.getMessage());
            } else if (throwable instanceof MailException) {
                return ResultBean.ofError("邮件发送失败");
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


    /**
     * 记录日志
     */
    private void log() {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//
//        String requestUrl = request.getScheme() //当前链接使用的协议
//                + "://" + request.getServerName()//服务器地址
//                + ":" + request.getServerPort() //端口号
//                + request.getContextPath() //应用名称，如果应用名称为
//                + request.getServletPath() //请求的相对url -->可作log记录:path(即：requestMapping路径)
//                + "?" + request.getQueryString(); //请求参数
//
//        logger.info(Arrays.asList(request.getServletPath(), JSON.toJSONString(requestUrl)).stream().collect(Collectors.joining("-")));
//
//        logger.info(Arrays.asList(request.getServletPath(), JSON.toJSONString(request.getQueryString())).stream().collect(Collectors.joining("-")));


        charReader(request);
    }


    private void charReader(HttpServletRequest request) {

        try {
            BufferedReader br = request.getReader();

            String str, wholeStr = "";
            while ((str = br.readLine()) != null) {
                wholeStr += str;
            }
            System.out.println(wholeStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
