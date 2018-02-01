package com.yunche.loan.config.constant;

/**
 * Created by zhouguoliang on 2018/2/1.
 */
public enum ProcessActionEnum {

    PASS("确认通过"),
    REJECT("驳回"),
    CANCEL("弃单"),
    EMAIL("发送邮件"),
    PRINT("打印");

    private String detail;

    ProcessActionEnum(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
