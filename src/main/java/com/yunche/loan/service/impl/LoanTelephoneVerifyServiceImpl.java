package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.LoanTelephoneVerifyDO;
import com.yunche.loan.mapper.LoanTelephoneVerifyDOMapper;
import com.yunche.loan.service.LoanTelephoneVerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/4/12
 */
@Service
public class LoanTelephoneVerifyServiceImpl implements LoanTelephoneVerifyService {

    @Autowired
    private LoanTelephoneVerifyDOMapper loanTelephoneVerifyDOMapper;


    @Override
    @Transactional
    public ResultBean<Void> save(LoanTelephoneVerifyDO loanTelephoneVerifyDO) {

        EmployeeDO employeeDO = SessionUtils.getLoginUser();

        loanTelephoneVerifyDO.setGmtModify(new Date());
        loanTelephoneVerifyDO.setUserId(employeeDO.getId());
        loanTelephoneVerifyDO.setUserName(employeeDO.getName());

        LoanTelephoneVerifyDO existDO = loanTelephoneVerifyDOMapper.selectByPrimaryKey(Long.valueOf(loanTelephoneVerifyDO.getOrderId()));
        if (null == existDO) {
            // create
            loanTelephoneVerifyDO.setGmtCreate(new Date());
            int count = loanTelephoneVerifyDOMapper.insertSelective(loanTelephoneVerifyDO);
            Preconditions.checkArgument(count > 0, "保存失败");
        } else {
            // update
            int count = loanTelephoneVerifyDOMapper.updateByPrimaryKeySelective(loanTelephoneVerifyDO);
            Preconditions.checkArgument(count > 0, "保存失败");
        }

        return ResultBean.ofSuccess(null);
    }
}
