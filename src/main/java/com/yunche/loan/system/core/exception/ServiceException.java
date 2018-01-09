package com.yunche.loan.system.core.exception;

import com.yunche.loan.system.core.ExceptionEnum;

/**
 * 服务（业务）异常如“ 账号或密码错误 ”，该异常只做INFO级别的日志记录 @see WebMvcConfigurer
 */
public class ServiceException extends RuntimeException {
    private String code;

    private String message;

    public ServiceException(ExceptionEnum error){
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public ServiceException(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
