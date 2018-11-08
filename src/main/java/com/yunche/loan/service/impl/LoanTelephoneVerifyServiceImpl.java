package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.LoanTelephoneVerifyParam;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanTelephoneVerifyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private PartnerDOMapper partnerDOMapper;

    @Override
    @Transactional
    public ResultBean<Void> save(LoanTelephoneVerifyParam loanTelephoneVerifyParam) {

        LoanTelephoneVerifyDO loanTelephoneVerifyDO = new LoanTelephoneVerifyDO();
        BeanUtils.copyProperties(loanTelephoneVerifyParam, loanTelephoneVerifyDO);

        Preconditions.checkNotNull(loanTelephoneVerifyParam.getOrderId(),"订单不能为空");
        //判断风险金加成比例不能大于100%
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(loanTelephoneVerifyParam.getOrderId()));
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(loanBaseInfoDO.getPartnerId(), new Byte("0"));
        BigDecimal riskBearRate = partnerDO.getRiskBearRate();
        if (loanTelephoneVerifyParam.getRiskSharingAddition().add(riskBearRate).compareTo(new BigDecimal(100))>0)
        {
            throw  new BizException("订单风险分担比例不能大于100%");
        }

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
        return ResultBean.ofSuccess(null);
    }
}
