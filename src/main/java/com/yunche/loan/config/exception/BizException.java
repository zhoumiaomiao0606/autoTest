package com.yunche.loan.config.exception;

public class BizException extends RuntimeException {

    private String msg;

    public BizException() {
        super();
    }

    public BizException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
