package com.yunche.loan.common;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public enum Code {

    Error("ERROR"),
    Success("SUCCESS");

    private String code;

    private Code(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
