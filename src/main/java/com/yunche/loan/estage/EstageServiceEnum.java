package com.yunche.loan.estage;

/**
 * Description:
 * author: yu.hb
 * Date: 2019-03-07
 */
public enum EstageServiceEnum {
    CREDIT_APPLY("1001","征信查询接口")

    ;

    private String busiCode;
    private String name;

    EstageServiceEnum(String busiCode, String name) {
        this.busiCode = busiCode;
        this.name = name;
    }

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
