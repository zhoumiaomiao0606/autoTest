package com.yunche.loan.config.constant;

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
}
