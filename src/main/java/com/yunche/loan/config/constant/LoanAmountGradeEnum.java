package com.yunche.loan.config.constant;

/**
 * Created by zhouguoliang on 2018/1/31.
 * 贷款额度档次
 */
public enum LoanAmountGradeEnum {

    GRADE_ONE((byte) 1, "13W以下"),
    GRADE_TWO((byte) 2, "13至20W"),
    GRADE_THREE((byte) 3, "20W以上");

    private Byte level;

    private String name;

    LoanAmountGradeEnum(Byte level, String name) {
        this.level = level;
        this.name = name;
    }

    public static String getNameByLevel(Byte level) {

        for (LoanAmountGradeEnum e : LoanAmountGradeEnum.values()) {
            if (e.getLevel().equals(level)) {

                return e.getName();
            }
        }
        return null;
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
