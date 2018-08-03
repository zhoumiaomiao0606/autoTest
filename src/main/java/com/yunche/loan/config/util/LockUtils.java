package com.yunche.loan.config.util;

import com.google.common.collect.Lists;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author liuzhe
 * @date 2018/8/3
 */
@Component
public class LockUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 获取🔐
     *
     * @param key
     * @param timeOut 单位：微秒
     */
    public boolean lock(String key, String val, Integer timeOut) {

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/distributedLock.lua")));
        redisScript.setResultType(Long.TYPE);

        Object result = stringRedisTemplate.execute(redisScript, Lists.newArrayList(key), val, String.valueOf(timeOut));

        if ((long) result == 1) {
            return true;
        }
        return false;
    }

    /**
     * 释放🔐
     *
     * @param key
     * @param val
     * @return
     */
    public boolean releaseLock(String key, String val) {

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/releaseLock.lua")));
        redisScript.setResultType(Long.TYPE);

        Object result = stringRedisTemplate.execute(redisScript, Lists.newArrayList(key), val);

        if ((long) result == 1) {
            return true;
        }
        return false;
    }
}
