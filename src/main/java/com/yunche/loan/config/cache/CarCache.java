package com.yunche.loan.config.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 车型库缓存
 *
 * @author liuzhe
 * @date 2018/2/2
 */
@Component
public class CarCache {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void refresh() {

    }


}
