package com.yunche.loan.config.constant;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/3/8
 */
public class BankConst {

    public static final Map<Long, String> BANK_MAP = Maps.newHashMap();

    static {
        BANK_MAP.put(1L, "中国工商银行杭州城站支行");
        BANK_MAP.put(2L, "中国工商银行哈尔滨顾乡支行");
        BANK_MAP.put(3L, "中国工商银行台州路桥支行");
        BANK_MAP.put(4L, "中国工商银行南京江宁支行");
    }

    public static final List<String> BANK_LIST = Lists.newArrayList();

    static {
        BANK_LIST.add("中国工商银行杭州城站支行");
        BANK_LIST.add("中国工商银行哈尔滨顾乡支行");
        BANK_LIST.add("中国工商银行台州路桥支行");
        BANK_LIST.add("中国工商银行南京江宁支行");
    }
}
