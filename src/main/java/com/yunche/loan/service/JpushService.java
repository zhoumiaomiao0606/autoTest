package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

public interface JpushService {

    public void push(Long employeeId,Long orderId,String title,String prompt,String msg,String processKey,Byte type);

    public ResultBean list(Integer pageIndex, Integer pageSize);

    public void read(Long id);
}
