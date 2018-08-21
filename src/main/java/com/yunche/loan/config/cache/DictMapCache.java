package com.yunche.loan.config.cache;

import com.google.common.collect.Maps;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.DictMapDO;
import com.yunche.loan.mapper.DictMapDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class DictMapCache {

    private static final String DICT_2_BANK = "area:cache:dict2bank";

    @Autowired
    DictMapDOMapper dictMapDOMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 刷新ALL_AREA缓存
     */
    @PostConstruct
    public void refreshAll() {

        // 获取所有行政区
        List<DictMapDO> allDictMap = dictMapDOMapper.getAll();

        if (CollectionUtils.isEmpty(allDictMap)) {
            return;
        }

        Map<String, String> map = Maps.newHashMap();

        allDictMap.parallelStream().forEach(e -> {
            String key = e.getItemKey() + "_" + e.getSource();
            String value = e.getTarget();
            map.put(key, value);
        });

        stringRedisTemplate.opsForHash().putAll(DICT_2_BANK, map);
    }

    /**
     * @param key
     * @param source
     * @return
     */
    public String getValue(String key, String source) {

        try {
            return stringRedisTemplate.opsForHash().get(DICT_2_BANK, key.trim() + "_" + source.trim()).toString();
        } catch (Exception e) {
            throw new BizException("数据字典转换异常,请联系管理员");
        }
    }
}
