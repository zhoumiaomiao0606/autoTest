package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yunche.loan.domain.entity.BankDO;
import com.yunche.loan.mapper.BankDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/5/15
 */
@Component
public class BankCache {

    private static final Logger logger = LoggerFactory.getLogger(BankCache.class);

    private static final String BANK_NAME_ALL_CACHE_KEY = "all:cache:bank:list:name";

    private static final String BANK_NAME_ID_MAP_CACHE_KEY = "all:cache:bank:map:name-id";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private BankDOMapper bankDOMapper;


    /**
     * 获取银行名称列表
     *
     * @return
     */
    public List<String> getAllBankName() {

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(BANK_NAME_ALL_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, String.class);
        }

        refreshListBankName();

        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, String.class);
        }

        return Collections.EMPTY_LIST;
    }

    @PostConstruct
    public void refresh() {
        refreshListBankName();
        refreshBankNameIdMap();
    }

    private void refreshListBankName() {

        List<BankDO> bankList = bankDOMapper.listAll(VALID_STATUS);

        if (!CollectionUtils.isEmpty(bankList)) {

            List<String> bankNameList = bankList.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {
                        return e.getName();
                    })
                    .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(bankNameList)) {
                BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(BANK_NAME_ALL_CACHE_KEY);
                boundValueOps.set(JSON.toJSONString(bankNameList));
            }
        }
    }

    /**
     * 通过bankName获取bankId
     *
     * @param bankName
     * @return
     */
    public Long getBankIdByName(String bankName) {

        Map<String, Integer> nameIdMap = getNameIdMap();

        if (!CollectionUtils.isEmpty(nameIdMap)) {

            Integer bankId = nameIdMap.get(bankName);

            if (null == bankId) {
                return null;
            }
            return Long.valueOf(bankId);
        }

        return null;
    }

    /**
     * 获取name-id映射
     *
     * @return
     */
    public Map<String, Integer> getNameIdMap() {

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(BANK_NAME_ID_MAP_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }

        refreshBankNameIdMap();

        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }

        return null;
    }

    private void refreshBankNameIdMap() {

        List<BankDO> bankList = bankDOMapper.listAll(VALID_STATUS);

        Map<String, Long> nameIdMap = Maps.newHashMap();

        if (!CollectionUtils.isEmpty(bankList)) {

            bankList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        nameIdMap.put(e.getName(), e.getId());
                    });

            if (!CollectionUtils.isEmpty(nameIdMap)) {
                BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(BANK_NAME_ID_MAP_CACHE_KEY);
                boundValueOps.set(JSON.toJSONString(nameIdMap));
            }
        }
    }

}
