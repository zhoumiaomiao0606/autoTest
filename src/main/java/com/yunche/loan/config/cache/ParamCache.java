package com.yunche.loan.config.cache;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.UniversalParamDO;
import com.yunche.loan.mapper.UniversalParamDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ParamCache {

    private static final String PARAM_DICT = "param:cache:universal_param";


    @Autowired
    private UniversalParamDOMapper universalParamDOMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 刷新ALL_AREA缓存
     */
    @PostConstruct
    public void refreshAll() {


        List<UniversalParamDO> universalParamDOS = universalParamDOMapper.allParam();



        if (CollectionUtils.isEmpty(universalParamDOS)) {
            return;
        }

        Map<String, String> map = Maps.newHashMap();

        universalParamDOS.parallelStream().forEach(e -> {
            String key = e.getParamId();
            String value = e.getParamValue();
            map.put(key, value);
        });

        stringRedisTemplate.opsForHash().putAll(PARAM_DICT, map);
    }

    /**
     * @param key
     * @return
     */
    public String getParam(String key) {

        try {
            return stringRedisTemplate.opsForHash().get(PARAM_DICT, key.trim()).toString();
        } catch (Exception e) {
            throw new BizException("数据字典转换异常,请联系管理员");
        }
    }

    /**
     * @param key
     * @return
     */
    public Set<String> getParam2List(String key) {
        Set<String> strs = Sets.newHashSet();
        try {
            String s = stringRedisTemplate.opsForHash().get(PARAM_DICT, key.trim()).toString();
            if(!StringUtils.isEmpty(s)){
                String[] split = s.split(",");
                for(String tmp :split){
                    strs.add(tmp.trim());
                }
            }
            return strs;
        } catch (Exception e) {
            throw new BizException("数据字典转换异常,请联系管理员");
        }
    }
}
