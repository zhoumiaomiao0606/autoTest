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
        BANK_MAP.put(1L, "中国工商银行");
        BANK_MAP.put(2L, "中国农业银行");
        BANK_MAP.put(3L, "中国建设银行");
        BANK_MAP.put(4L, "中国人民银行");
        BANK_MAP.put(5L, "中国招商银行");
        BANK_MAP.put(6L, "杭州银行");
        BANK_MAP.put(7L, "网商银行");
    }

    public static final List<String> BANK_LIST = Lists.newArrayList();

    static {
        BANK_LIST.add("中国工商银行");
        BANK_LIST.add("中国农业银行");
        BANK_LIST.add("中国建设银行");
        BANK_LIST.add("中国人民银行");
        BANK_LIST.add("中国招商银行");
        BANK_LIST.add("杭州银行");
        BANK_LIST.add("网商银行");
    }
}
