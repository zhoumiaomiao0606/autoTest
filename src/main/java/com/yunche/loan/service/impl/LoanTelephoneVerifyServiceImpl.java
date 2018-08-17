package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanTelephoneVerifyDO;
import com.yunche.loan.domain.param.LoanTelephoneVerifyParam;
import com.yunche.loan.mapper.EmployeeDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanTelephoneVerifyDOMapper;
import com.yunche.loan.service.LoanTelephoneVerifyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/4/12
 */
@Service
public class LoanTelephoneVerifyServiceImpl implements LoanTelephoneVerifyService {

    @Autowired
    private LoanTelephoneVerifyDOMapper loanTelephoneVerifyDOMapper;

    @Resource
    private EmployeeDOMapper employeeDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Override
    @Transactional
    public ResultBean<Void> save(LoanTelephoneVerifyParam loanTelephoneVerifyParam) {

        LoanTelephoneVerifyDO loanTelephoneVerifyDO = new LoanTelephoneVerifyDO();
        BeanUtils.copyProperties(loanTelephoneVerifyParam, loanTelephoneVerifyDO);

        EmployeeDO employeeDO = SessionUtils.getLoginUser();

        loanTelephoneVerifyDO.setGmtModify(new Date());
        loanTelephoneVerifyDO.setUserId(employeeDO.getId());
        loanTelephoneVerifyDO.setUserName(employeeDO.getName());
        LoanTelephoneVerifyDO existLoanTelephoneVerifyDO = loanTelephoneVerifyDOMapper.selectByPrimaryKey(Long.valueOf(loanTelephoneVerifyParam.getOrderId()));
        if (null == existLoanTelephoneVerifyDO) {
            // create
            loanTelephoneVerifyDO.setGmtCreate(new Date());
            int count = loanTelephoneVerifyDOMapper.insertSelective(loanTelephoneVerifyDO);
            Preconditions.checkArgument(count > 0, "保存失败");
        } else {
            // update
            int count = loanTelephoneVerifyDOMapper.updateByPrimaryKeySelective(loanTelephoneVerifyDO);
            Preconditions.checkArgument(count > 0, "保存失败");
        }
        //更新客户信息 签单类型
        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(loanTelephoneVerifyDO.getOrderId()));
        LoanCustomerDO customerDO = loanCustomerDOMapper.selectByPrimaryKey(orderDO.getLoanCustomerId(), null);
        customerDO.setSignatureType(loanTelephoneVerifyParam.getSignatureType());
        int count = loanCustomerDOMapper.updateByPrimaryKeySelective(customerDO);
        Preconditions.checkArgument(count > 0, "保存失败");
        return ResultBean.ofSuccess(null);
    }
}
