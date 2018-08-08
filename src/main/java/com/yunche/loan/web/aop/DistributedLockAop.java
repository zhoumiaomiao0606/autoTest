package com.yunche.loan.web.aop;

import com.google.common.collect.Maps;
import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.config.util.LockUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;

/**
 * 分布式🔐
 *
 * @author liuzhe
 * @date 2018/8/3
 */
@Aspect
@Component
public class DistributedLockAop {

    /**
     * @see com.yunche.loan.config.anno.DistributedLock
     * <p>
     * methodName - DefaultValue   Map
     */
    private static Map<String, Object> methodName_DefaultValue_Map = Maps.newHashMap();


    @Autowired
    private LockUtils lockUtils;


    @Around(value = "@annotation(com.yunche.loan.config.anno.DistributedLock)")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        DistributedLock annotation = method.getAnnotation(DistributedLock.class);

        String key = annotation.key();
        long timeOut = annotation.timeOut();

        // key
        Object defaultValue_key = methodName_DefaultValue_Map.get("key");
        if (defaultValue_key.equals(key) || StringUtils.isBlank(key)) {

            // methodName
            String fullMethodName = getFullMethodName(method);

            // hashCode
            int fullMethodNameHashCode = fullMethodName.hashCode();

            // prefix + methodName + hashCode
            key = defaultValue_key + method.getName() + ":" + fullMethodNameHashCode;
        }

        // 生成一个随机数：作为当前🔐的val
        long randomNum = new Random().nextInt(1000000000);
        String val = System.currentTimeMillis() + String.valueOf(randomNum);


        try {

            // 获取锁
            boolean getLock = lockUtils.lock(key, val, timeOut);

            // 获取到锁
            if (getLock) {

                // 执行原方法
                Object result = point.proceed();

                return result;
            }

        } catch (Exception ex) {

            throw ex;

        } finally {

            // 释放锁
            boolean releaseLock = lockUtils.releaseLock(key, val);
        }

        return null;
    }

    /**
     * method的全方法名
     *
     * @param method
     * @return
     */
    private static String getFullMethodName(Method method) {

        String fullMethodName = "";

        String clazzName = method.getDeclaringClass().getName();
        String methodName = method.getName();

        fullMethodName += clazzName;
        fullMethodName += methodName;

        Class<?>[] parameterTypes = method.getParameterTypes();

        if (ArrayUtils.isNotEmpty(parameterTypes)) {

            for (int i = 0; i < parameterTypes.length; i++) {

                fullMethodName += parameterTypes[i].getName();
            }
        }

        return fullMethodName;
    }

    /**
     * 初始化：methodName_DefaultValue_Map
     *
     * @throws NoSuchFieldException
     */
    @PostConstruct
    public static void init() {

        Class<DistributedLock> clazz = DistributedLock.class;

        Method[] methods = clazz.getDeclaredMethods();

        for (int i = 0; i < methods.length; i++) {

            Method method = methods[i];

            String methodName = method.getName();
            Object defaultValue = method.getDefaultValue();

            methodName_DefaultValue_Map.put(methodName, defaultValue);
        }
    }

}
