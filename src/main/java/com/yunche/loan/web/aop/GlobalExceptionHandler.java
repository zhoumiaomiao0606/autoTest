package com.yunche.loan.web.aop;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
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


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResultBean handleMethodArgumentNotValidException(Exception e) {

        logger.error("GlobalExceptionHandler : ", e);

        if (e instanceof BizException) {
            return ResultBean.ofError(e.getMessage());
        } else if (e instanceof MissingServletRequestParameterException) {
            return ResultBean.ofError("GET 缺少接口规范必要参数");
        } else if (e instanceof MethodArgumentNotValidException) {
            return ResultBean.ofError("POST 缺少接口规范必要参数");
        } else if (e instanceof IllegalArgumentException) {
            return ResultBean.ofError(e.getMessage());
        } else if (e instanceof NullPointerException) {
            return ResultBean.ofError(e.getMessage());
        } else if (e instanceof MailException) {
            return ResultBean.ofError("邮件发送失败");
        } else if (e instanceof BadSqlGrammarException) {
            return ResultBean.ofError("糟糕，出错啦！");
        } else if (e instanceof RuntimeException) {
            return ResultBean.ofError("糟糕，出错啦！");
        } else {
            String errorMsg = e.toString() == null ? e.getMessage() : e.toString();
            return ResultBean.ofError(errorMsg == null || errorMsg.equals("") ? "未知错误" : e.toString());
        }
    }

}
