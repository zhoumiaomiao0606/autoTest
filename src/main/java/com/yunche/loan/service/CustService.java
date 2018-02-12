package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.CustRelaPersonInfoVO;
import org.activiti.engine.delegate.DelegateExecution;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
public interface CustService {

    ResultBean<Long> create(DelegateExecution execution);

    ResultBean<Long> createMainCust(CustBaseInfoVO custBaseInfoVO);

    ResultBean<Long> updateMainCust(CustBaseInfoVO custBaseInfoVO);

    ResultBean<Long> createRelaCust(CustRelaPersonInfoVO custRelaPersonInfoVO);

    ResultBean<Long> updateRelaCust(CustRelaPersonInfoVO custRelaPersonInfoVO);

}
