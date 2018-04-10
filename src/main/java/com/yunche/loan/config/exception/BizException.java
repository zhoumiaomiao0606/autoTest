package com.yunche.loan.config.exception;

public class BizException extends RuntimeException {

    private String code;

    private String msg;

    public BizException() {
        super();
    }

    public BizException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BizException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
