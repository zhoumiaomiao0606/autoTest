package com.yunche.loan.web.aop;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        long startTime = System.currentTimeMillis();

        // 日志记录
        log(pjp.getArgs());

        // exec
        Object result = pjp.proceed();

        // 统计时间
        long totalTime = System.currentTimeMillis() - startTime;
        logger.info("totalTime : {}s", new Double(totalTime) / 1000);

        return result;
    }

    /**
     * 记录日志
     *
     * @param args
     */
    private void log(Object[] args) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        List<Object> argList = Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse))
                .map(arg -> {
                    return arg;
                })
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(argList)) {
            logger.info(Arrays.asList(request.getServletPath(), getIpAddress(request)).stream().collect(Collectors.joining(" ")));
        } else {
            logger.info(Arrays.asList(request.getServletPath(), getIpAddress(request), JSON.toJSONString(argList.get(0))).stream().collect(Collectors.joining(" ")));
        }
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request
     * @return
     * @throws IOException
     */
    public final static String getIpAddress(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ipIsBlank(ip)) {

            ip = request.getHeader("Proxy-Client-IP");

            if (ipIsBlank(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (ipIsBlank(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }

            if (ipIsBlank(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }

            if (ipIsBlank(ip)) {
                ip = request.getRemoteAddr();
            }

        } else if (ip.length() > 15) {

            // 经过多层代理后会有多个代理，取第一个ip地址就可以了
            String[] ips = ip.split("\\,");

            for (int index = 0; index < ips.length; index++) {

                String strIp = ips[index];

                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }

        return ip;
    }

    private static boolean ipIsBlank(String ip) {

        boolean ipIsBank = StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip);

        return ipIsBank;
    }

}
