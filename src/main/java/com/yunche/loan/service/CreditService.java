package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import org.activiti.engine.delegate.DelegateExecution;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
public interface CreditService {

    ResultBean<Void> create(DelegateExecution execution);

}
