package com.yunche.loan.service.impl;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.CreditService;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
@Service
@Transactional
public class CreditServiceImpl implements CreditService {
    @Override
    public ResultBean<Void> test(DelegateExecution execution) {
        return null;
    }
}
