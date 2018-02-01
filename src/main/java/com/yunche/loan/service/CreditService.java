package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BizModelQuery;
import com.yunche.loan.domain.viewObj.BizModelVO;
import org.activiti.engine.delegate.DelegateExecution;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
public interface CreditService {

    ResultBean<Void> create(DelegateExecution execution);

}
