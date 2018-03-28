package com.yunche.loan.config.constant;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/3/28
 */
public class MappingConst {

    /**
     * 1-电审增补;2-送银行资料缺少;3-银行退件;4-上门家访资料增补;5-费用调整;
     */
    public static final Map<String, String> SUPPLEMENT_TYPE_TEXT_MAP = Maps.newHashMap();

    static {
        SUPPLEMENT_TYPE_TEXT_MAP.put("1", "电审增补");
        SUPPLEMENT_TYPE_TEXT_MAP.put("2", "送银行资料缺少");
        SUPPLEMENT_TYPE_TEXT_MAP.put("3", "银行退件");
        SUPPLEMENT_TYPE_TEXT_MAP.put("4", "上门家访资料增补");
        SUPPLEMENT_TYPE_TEXT_MAP.put("5", "费用调整");
    }
}
