package com.yunche.loan.system.adapter;

import com.yunche.loan.system.core.ExceptionEnum;
import com.yunche.loan.system.core.exception.ServiceException;
import com.yunche.loan.system.core.templete.ResponseTemplete;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Created by WangGang on 2017/6/22 0022.
 * E-mail userbean@outlook.com
 * The final interpretation of this procedure is owned by the author
 */
@RestControllerAdvice
public class GlobalExceptionAdapter {

    private Logger logger = Logger.getLogger(GlobalExceptionAdapter.class);


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseTemplete handleTransferException(Exception e) {

        if (e instanceof NoHandlerFoundException) {
            ResponseTemplete responseTemplete = new ResponseTemplete();
            responseTemplete.setService_code(ExceptionEnum.EC00000404.getCode());
            responseTemplete.setService_message(ExceptionEnum.EC00000404.getMessage());
            return responseTemplete;
        } else if(e instanceof ServiceException){
            ServiceException exception = (ServiceException)e;
            ResponseTemplete responseTemplete = new ResponseTemplete();
            responseTemplete.setService_code(exception.getCode());
            responseTemplete.setService_message(exception.getMessage());
            return responseTemplete;
        }else {
            ResponseTemplete responseTemplete = new ResponseTemplete();
            responseTemplete.setService_code(ExceptionEnum.EC00000500.getCode());
            responseTemplete.setService_message(ExceptionEnum.EC00000500.getMessage());
            return responseTemplete;
        }




    }


}
