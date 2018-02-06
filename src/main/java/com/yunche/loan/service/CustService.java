package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import org.activiti.engine.delegate.DelegateExecution;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
public interface CustService {

    ResultBean<Void> create(DelegateExecution execution);

    ResultBean<Void> update(CustBaseInfoVO custBaseInfoVO);

}
