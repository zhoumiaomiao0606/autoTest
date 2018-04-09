package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/4/9
 */
@Component
public class ActivitiCache {

    private static final Map<String, List<String>> canditate = Maps.newHashMap();

    /**
     * activiti节点-候选组-缓存KEY
     */
    private static final String CANDIDATE_GROUP_CACHE_KEY = "activiti:candidate:group";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

//    @Autowired
//    private


    public Map<String, List<String>> get() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CANDIDATE_GROUP_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }

        // 刷新缓存
        refresh();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }

        return null;
    }

    /**
     * 刷新缓存
     */
    private void refresh() {


    }

}
