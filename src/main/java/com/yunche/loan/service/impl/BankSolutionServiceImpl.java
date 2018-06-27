package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.BankSolutionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Transactional
public class BankSolutionServiceImpl implements BankSolutionService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Override
    public void execute(@Validated @NotNull Long bankId, @Validated @NotNull List<LoanCustomerDO> customers) {
        checkCustomerHavingCreditON14Day(customers);
        int value = bankId.intValue();
        switch (value) {
            case 4:
                //判断当前客户贷款银行是否为台州工行，如为台州工行：
                tzICBCBankProcess(customers);
                break;
            default:
                return;
        }
    }

    @Override
    public void compensation(@Validated @NotNull Long bankId,@Validated @NotNull Long customerId) {

    }


    private void tzICBCBankProcess(List<LoanCustomerDO> customers){
        //1、提交征信申请后，系统判断当前客户14天内是否存在贷款申请人，如存在，则提示客户“当前客户14天内存在征信申请，不能重复提交”。
    }

    private void checkCustomerHavingCreditON14Day(List<LoanCustomerDO> customers){
        for(LoanCustomerDO loanCustomerDO:customers){
            Preconditions.checkArgument(StringUtils.isNotBlank(loanCustomerDO.getIdCard()), loanCustomerDO.getName()+"身份证号不能为空");
            if(loanQueryDOMapper.checkCustomerHavingCreditON14Day(loanCustomerDO.getIdCard())){
                throw new BizException(loanCustomerDO.getName()+"在14天内重复查询征信");
            }
        }
    }
}
