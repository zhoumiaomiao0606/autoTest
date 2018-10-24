package com.yunche.loan.web.aspect;

import com.alibaba.fastjson.JSON;

import com.yunche.loan.config.util.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一日志处理
 *
 * @author liuzhe
 * @date 2018/1/10
 */
@Aspect
@Component
public class BizLogHandler {

    private static final Logger logger = LoggerFactory.getLogger(BizLogHandler.class);


    @Around(value = "execution(* com.yunche.loan.web.controller..*.*(..))")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        long startTime = System.currentTimeMillis();

        // 日志记录
        log(pjp);

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
     * @param pjp
     */
    private void log(ProceedingJoinPoint pjp) {

        Object[] args = pjp.getArgs();

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        MessageMapping messageMappingAnnotation = method.getAnnotation(MessageMapping.class);

        // web
        if (null == messageMappingAnnotation) {

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            List<Object> argList = Arrays.stream(args)
                    .filter(arg -> !(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(argList)) {
                logger.info(Arrays.asList(request.getServletPath(), getIpAddress(request)).stream().collect(Collectors.joining(" ")));
            } else {
                logger.info(Arrays.asList(request.getServletPath(), getIpAddress(request), JSON.toJSONString(argList)).stream().collect(Collectors.joining(" ")));
            }
        }

        // webSocket
        else {

            String[] path = messageMappingAnnotation.value();

            String webSocketSessionId = SessionUtils.getWebSocketSessionId();

            String currentServerHostAddress = null;
            try {
                currentServerHostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                logger.error(e.getMessage(), e);
            }

            logger.info(Arrays.asList(path[0], webSocketSessionId, currentServerHostAddress, JSON.toJSONString(args))
                    .stream().collect(Collectors.joining(" ")));
        }

    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getIpAddress(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ipIsBlank(ip)) {
            ip = request.getHeader("Proxy-Client-IP");

            if (ipIsBlank(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");

                if (ipIsBlank(ip)) {
                    ip = request.getHeader("HTTP_CLIENT_IP");

                    if (ipIsBlank(ip)) {
                        ip = request.getHeader("HTTP_X_FORWARDED_FOR");

                        if (ipIsBlank(ip)) {
                            ip = request.getRemoteAddr();
                        }
                    }
                }
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
