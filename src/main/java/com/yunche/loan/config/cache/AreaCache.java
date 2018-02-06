package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 省市-城市缓存
 *
 * @author liuzhe
 * @date 2018/2/2
 */
@Component
public class AreaCache {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @PostConstruct
    public void test() {

//        System.out.println(JSON.toJSONString(stringRedisTemplate));
    }


}
