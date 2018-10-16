package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.service.BankInterfaceSerialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liuzhe
 * @date 2018/10/10
 */
@Service
public class BankInterfaceSerialServiceImpl implements BankInterfaceSerialService {

    @Autowired
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;


    @Override
    @Transactional
    public void update(BankInterfaceSerialDO bankInterfaceSerialDO) {
        Preconditions.checkNotNull(bankInterfaceSerialDO, "serialNo不能为空");
        Preconditions.checkNotNull(bankInterfaceSerialDO.getSerialNo(), "serialNo不能为空");

        int count = bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
        Preconditions.checkArgument(count > 0, "更新失败");
    }
}
