package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
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

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/5/15
 */
@Component
public class BankCache {

    private static final Logger logger = LoggerFactory.getLogger(BankCache.class);

    private static final String BANK_NAME_ALL_CACHE_KEY = "all:bank:name";

    private static final String CAR_BRAND_ALL_CACHE_KEY = "all:cache:car:brand";

    private static final String CAR_MODEL_ALL_CACHE_KEY = "all:cache:car:model";

    private static final String CAR_DETAIL_ALL_CACHE_KEY = "all:cache:car:detail";

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

        refresh();

        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, String.class);
        }

        return Collections.EMPTY_LIST;
    }

    public void refresh() {
        refreshBankName();
    }

    private void refreshBankName() {

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
}
