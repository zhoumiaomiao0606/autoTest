package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanTelephoneVerifyDO;
import com.yunche.loan.domain.param.LoanTelephoneVerifyParam;
import com.yunche.loan.mapper.LoanTelephoneVerifyDOMapper;
import com.yunche.loan.service.LoanTelephoneVerifyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ResultBean<Void> save(LoanTelephoneVerifyParam loanTelephoneVerifyParam) {

        LoanTelephoneVerifyDO loanTelephoneVerifyDO = new LoanTelephoneVerifyDO();
        BeanUtils.copyProperties(loanTelephoneVerifyParam, loanTelephoneVerifyDO);

        LoanTelephoneVerifyDO existLoanTelephoneVerifyDO = loanTelephoneVerifyDOMapper.selectByPrimaryKey(loanTelephoneVerifyParam.getOrderId());
        if (null == existLoanTelephoneVerifyDO) {
            // create
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
