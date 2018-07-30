package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.util.StringUtil;
import com.yunche.loan.domain.entity.ConfDictDO;
import com.yunche.loan.domain.vo.DataDictionaryVO;
import com.yunche.loan.mapper.ConfDictDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author liuzhe
 * @date 2018/7/10
 */
@Component
public class DictCache {

    private static final String DICT_CACHE_KEY = "dict:cache";

    @Autowired
    private ConfDictDOMapper confDictDOMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * all：node-roles 映射关系
     *
     * @return
     */
    public DataDictionaryVO get() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(DICT_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, DataDictionaryVO.class);
        }

        // 刷新cache
        refreshCache();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, DataDictionaryVO.class);
        }
        return null;
    }

    @PostConstruct
    public void refresh() {
        refreshCache();
    }

    private void refreshCache() {
        // get
        DataDictionaryVO dictionary = dictionary();

        if (null != dictionary) {

            // check NULL
            String cache = JSON.toJSONString(dictionary);
            DataDictionaryVO dataDictionaryVO = JSON.parseObject(cache, DataDictionaryVO.class);

            if (null != dataDictionaryVO) {
                // 刷新缓存
                BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(DICT_CACHE_KEY);
                boundValueOps.set(cache);
            }
        }
    }

    /**
     * get
     *
     * @return
     */
    public DataDictionaryVO dictionary() {

        DataDictionaryVO dataDictionaryVO = new DataDictionaryVO();

        // getAll
        List<ConfDictDO> confDictDOList = confDictDOMapper.getAll();

        // clazz对象
        Class<? extends DataDictionaryVO> clazz = dataDictionaryVO.getClass();

        // 获取clazz的所有字段
        Field[] fields = clazz.getDeclaredFields();
        List<String> fieldList = Arrays.stream(fields).map(e -> {
            return e.getName();
        }).collect(Collectors.toList());

        // 反射赋值
        if (!CollectionUtils.isEmpty(confDictDOList)) {

            confDictDOList.stream()
                    .filter(e -> null != e && StringUtils.isNotBlank(e.getField()) && fieldList.contains(e.getField()))
                    .forEach(e -> {

                        String field = e.getField();

                        String methodName = "set" + StringUtil.firstLetter2UpperCase(field);

                        try {
                            Method method = clazz.getMethod(methodName, DataDictionaryVO.Detail.class);

                            DataDictionaryVO.Detail detail = new DataDictionaryVO.Detail();
                            BeanUtils.copyProperties(e, detail);

                            Object result = method.invoke(dataDictionaryVO, detail);

                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    });
        }

        return dataDictionaryVO;
    }
}
