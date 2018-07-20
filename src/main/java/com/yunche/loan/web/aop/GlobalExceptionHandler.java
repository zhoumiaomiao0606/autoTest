package com.yunche.loan.web.aop;

import com.alibaba.fastjson.JSONPathException;
import com.fasterxml.jackson.core.JsonParseException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import feign.codec.DecodeException;
import org.activiti.engine.ActivitiException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.mail.MailException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResultBean handleMethodArgumentNotValidException(Exception e) {

        logger.error("GlobalExceptionHandler : ", e);

        if (e instanceof BizException) {
            String code = ((BizException) e).getCode();
            String msg = ((BizException) e).getMsg();
            if (null == code) {
                return ResultBean.ofError(msg);
            } else {
                return ResultBean.of(null, false, code, msg);
            }
        } else if (e instanceof MissingServletRequestParameterException) {
            return ResultBean.ofError("必入参数未填写");
        } else if (e instanceof MethodArgumentNotValidException) {
            return ResultBean.ofError("必入参数未填写");
        } else if (e instanceof DecodeException) {
            return ResultBean.ofError(e.getMessage());
        }else if (e instanceof ConstraintViolationException) {
            return ResultBean.ofError(e.getMessage());
        } else if (e instanceof IllegalArgumentException) {
            return ResultBean.ofError(e.getMessage());
        } else if (e instanceof NullPointerException) {
            return ResultBean.ofError(e.getMessage());
        } else if (e instanceof MailException) {
            return ResultBean.ofError("邮件发送失败");
        } else if (e instanceof ActivitiException) {
            return ResultBean.ofError("流程审核参数有误");
        } else if (e instanceof NumberFormatException) {
            return ResultBean.ofError("参数类型转换异常");
        } else if (e instanceof JsonParseException) {
            return ResultBean.ofError("参数类型转换异常");
        } else if (e instanceof JSONPathException) {
            return ResultBean.ofError("类型转换异常");
        } else if (e instanceof BadSqlGrammarException) {
            return ResultBean.ofError("服务器异常,请联系管理员!");
        } else if (e instanceof MySQLSyntaxErrorException) {
            return ResultBean.ofError("服务器异常,请联系管理员!");
        } else if (e instanceof RuntimeException) {
            return ResultBean.ofError("服务器异常,请联系管理员!");
        } else {
            String errorMsg = e.toString() == null ? e.getMessage() : e.toString();
            return ResultBean.ofError(StringUtils.isBlank(errorMsg) ? "未知错误" : errorMsg);
        }
    }

}
