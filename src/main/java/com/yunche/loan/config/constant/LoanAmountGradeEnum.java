package com.yunche.loan.config.constant;

/**
 * Created by zhouguoliang on 2018/1/31.
 * 贷款额度档次
 */
public enum LoanAmountGradeEnum {

    GRADE_ONE(1, "13W以下"),
    GRADE_TWO(2, "13至20W"),
    GRADE_THREE(3, "20W以上");

    private Integer level;

    private String name;

    LoanAmountGradeEnum(Integer level, String name) {
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
