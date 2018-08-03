package com.yunche.loan.config.constant;

import com.yunche.loan.config.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhouguoliang on 2018/1/31.
 * 购车类型
 */
public enum CarTypeEnum {

    NEW_CAR(0, "新车"),
    SECOND_HAND(1, "二手车"),
    ALL(2, "不限");

    private Integer level;

    private String name;

    CarTypeEnum(Integer level, String name) {
        this.level = level;
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public static String getValueByKey(String key) {
        if(StringUtils.isBlank(key)){
            return "未知";
        }

        for (CarTypeEnum e : CarTypeEnum.values()) {
            if (e.level.intValue() == Integer.valueOf(key).intValue()) {
                return e.name;
            }
        }
        return "未知";
    }
}
