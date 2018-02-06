package com.yunche.loan.config.constant;

/**
 * Created by zhouguoliang on 2018/2/1.
 */
public enum ProcessActionEnum {

    TODO("未处理"),
    PASS("通过"),
    REJECT("打回修改"),
    CANCEL("弃单"),
    SUPPLEMENT("增补资料"),
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
