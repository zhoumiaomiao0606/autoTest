package com.yunche.loan.config.constant;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/3/28
 */
public class MappingConst {

    /**
     * 资料增补类型：1-电审增补;2-送银行资料缺少;3-银行退件;4-上门家访资料增补;5-费用调整;
     * <p>
     * 表：loan_info_supplement  -type
     */
    public static final Map<Byte, String> SUPPLEMENT_TYPE_TEXT_MAP = Maps.newHashMap();

    /**
     * 房产性质：1-自购商品房;2-分期付款购房;3-自建房;4-房改房;5-居住父母家;6-居住亲朋家;7-单位集体宿舍;8-租房;9-其他;
     * <p>
     * 表：loan_customer   -house_feature
     */
    public static final Map<Byte, String> HOUSE_FEATURE_TYPE_TEXT_MAP = Maps.newHashMap();


    /**
     * 房产所有人：1-本人所有;2-夫妻共有;3-配偶所有;4-父母所有;5-其他;
     * <p>
     * 表：loan_customer   -house_owner
     */
    public static final Map<Byte, String> HOUSE_OWNER_TYPE_TEXT_MAP = Maps.newHashMap();

    static {
        SUPPLEMENT_TYPE_TEXT_MAP.put((byte) 1, "电审增补");
        SUPPLEMENT_TYPE_TEXT_MAP.put((byte) 2, "送银行资料缺少");
        SUPPLEMENT_TYPE_TEXT_MAP.put((byte) 3, "银行退件");
        SUPPLEMENT_TYPE_TEXT_MAP.put((byte) 4, "上门家访资料增补");
        SUPPLEMENT_TYPE_TEXT_MAP.put((byte) 5, "费用调整");


        HOUSE_FEATURE_TYPE_TEXT_MAP.put((byte) 1, "自购商品房");
        HOUSE_FEATURE_TYPE_TEXT_MAP.put((byte) 2, "分期付款购房");
        HOUSE_FEATURE_TYPE_TEXT_MAP.put((byte) 3, "自建房");
        HOUSE_FEATURE_TYPE_TEXT_MAP.put((byte) 4, "房改房");
        HOUSE_FEATURE_TYPE_TEXT_MAP.put((byte) 5, "居住父母家");
        HOUSE_FEATURE_TYPE_TEXT_MAP.put((byte) 6, "居住亲朋家");
        HOUSE_FEATURE_TYPE_TEXT_MAP.put((byte) 7, "单位集体宿舍");
        HOUSE_FEATURE_TYPE_TEXT_MAP.put((byte) 8, "租房");
        HOUSE_FEATURE_TYPE_TEXT_MAP.put((byte) 9, "其他");

        HOUSE_OWNER_TYPE_TEXT_MAP.put((byte) 1, "本人所有");
        HOUSE_OWNER_TYPE_TEXT_MAP.put((byte) 2, "夫妻共有");
        HOUSE_OWNER_TYPE_TEXT_MAP.put((byte) 3, "配偶所有");
        HOUSE_OWNER_TYPE_TEXT_MAP.put((byte) 4, "父母所有");
        HOUSE_OWNER_TYPE_TEXT_MAP.put((byte) 5, "其他");
    }
}
