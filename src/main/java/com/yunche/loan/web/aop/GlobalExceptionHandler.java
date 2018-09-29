package com.yunche.loan.web.aop;

import com.alibaba.fastjson.JSONPathException;
import com.fasterxml.jackson.core.JsonParseException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

/**
 * 统一异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResultBean<Object> exceptionHandler(Throwable e) {

        return doGlobalExceptionHandler(e);
    }

    /**
     * 执行异常处理
     *
     * @param e
     * @return
     */
    public static ResultBean<Object> doGlobalExceptionHandler(Throwable e) {

        logger.error(e.getMessage(), e);

        if (e instanceof BizException) {
            String code = ((BizException) e).getCode();
            String msg = ((BizException) e).getMsg();
            if (null == code) {
                return ResultBean.ofError(msg);
            } else {
                return ResultBean.ofError(code, msg);
            }
        } else if (e instanceof MissingServletRequestParameterException) {
            String parameterName = ((MissingServletRequestParameterException) e).getParameterName();
            return ResultBean.ofError(parameterName + "不能为空");
        } else if (e instanceof MethodArgumentNotValidException) {
            return ResultBean.ofError("表单必录数据填写不完整");
        } else if (e instanceof DecodeException) {
            return ResultBean.ofError(e.getMessage());
        } else if (e instanceof ConstraintViolationException) {
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
            return ResultBean.ofError("出错啦,请稍后再试!");
        } else if (e instanceof MySQLSyntaxErrorException) {
            return ResultBean.ofError("出错啦,请稍后再试!");
        } else if (e instanceof MySQLIntegrityConstraintViolationException) {
            return ResultBean.ofError("出错啦,请稍后再试!");
        } else if (e instanceof RuntimeException) {
            return ResultBean.ofError("出错啦,请稍后再试!");
        } else if (e instanceof Exception) {
            return ResultBean.ofError("出错啦,请稍后再试!");
        } else {
            String errorMsg = e.toString() == null ? e.getMessage() : e.toString();
            return ResultBean.ofError(StringUtils.isBlank(errorMsg) ? "未知错误" : errorMsg);
        }
    }

}
