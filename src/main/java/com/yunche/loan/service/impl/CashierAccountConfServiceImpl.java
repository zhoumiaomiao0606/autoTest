package com.yunche.loan.service.impl;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CashierAccountConfParam;
import com.yunche.loan.mapper.CashierAccountConfDOMapper;
import com.yunche.loan.service.CashierAccountConfService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CashierAccountConfServiceImpl implements CashierAccountConfService
{
    private CashierAccountConfDOMapper cashierAccountConfDOMapper;

    @Override
    public ResultBean<Long> create(CashierAccountConfParam cashierAccountConfParam)
    {
        return null;
    }

    @Override
    public ResultBean<Void> update(CashierAccountConfParam cashierAccountConfParam)
    {
        return null;
    }

    @Override
    public ResultBean<Void> delete(Long id)
    {
        return null;
    }
}
