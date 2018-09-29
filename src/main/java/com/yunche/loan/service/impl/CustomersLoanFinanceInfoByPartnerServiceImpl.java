package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import com.yunche.loan.domain.param.CustomersLoanFinanceInfoByPartnerParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.CustomersLoanFinanceInfoByPartnerMapper;
import com.yunche.loan.mapper.LoanApplyCompensationDOMapper;
import com.yunche.loan.service.CustomersLoanFinanceInfoByPartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CustomersLoanFinanceInfoByPartnerServiceImpl implements CustomersLoanFinanceInfoByPartnerService
{
    @Autowired
    private CustomersLoanFinanceInfoByPartnerMapper customersLoanFinanceInfoByPartnerMapper;

    @Autowired
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;
    public static enum CustomersLoanFinance {
        BADBALANCE(1,"不良余额"),
        OVERDUEBALANCE(2,"逾期余额"),
        INGUARANTEEBALANCE(3,"在保余额"),
        LOANBALANCE(4,"贷款余额");

        private int code;

        private String message;

        CustomersLoanFinance(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    @Override
    public ResultBean selectCustomersLoanFinanceInfoByPartner(CustomersLoanFinanceInfoByPartnerParam customersLoanFinanceInfoByPartnerParam)
    {
        Preconditions.checkNotNull(customersLoanFinanceInfoByPartnerParam.getPartnerId(),"合伙人id不能为空");
        Preconditions.checkNotNull(customersLoanFinanceInfoByPartnerParam.getCode(),"查询类型不能为空");
        if (customersLoanFinanceInfoByPartnerParam.getCode() == CustomersLoanFinance.BADBALANCE.getCode())
        {
            List<BadBalanceByPartnerVO> badBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectBadBalance(customersLoanFinanceInfoByPartnerParam.getPartnerId());
            return ResultBean.ofSuccess(badBalanceByPartnerVOS);

        }else if (customersLoanFinanceInfoByPartnerParam.getCode() == CustomersLoanFinance.OVERDUEBALANCE.getCode())
        {
            List<OverdueBalanceByPartnerVO> overdueBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectOverdueBalance(customersLoanFinanceInfoByPartnerParam.getPartnerId());
            return ResultBean.ofSuccess(overdueBalanceByPartnerVOS);

        }else if (customersLoanFinanceInfoByPartnerParam.getCode() == CustomersLoanFinance.INGUARANTEEBALANCE.getCode())
        {
            List<InGuaranteeBalanceByPartnerVO> inGuaranteeBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectInGuaranteeBalance(customersLoanFinanceInfoByPartnerParam.getPartnerId());
            return ResultBean.ofSuccess(inGuaranteeBalanceByPartnerVOS);

        }else if (customersLoanFinanceInfoByPartnerParam.getCode() == CustomersLoanFinance.LOANBALANCE.getCode())
        {
            List<LoanBalanceByPartnerVO> loanBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectLoanBalance(customersLoanFinanceInfoByPartnerParam.getPartnerId());
            return ResultBean.ofSuccess(loanBalanceByPartnerVOS);

        }
        else
            {
                return ResultBean.ofError("参数有错误");
            }

    }

    @Override
    public ResultBean selectTotalLoanFinanceInfoByPartner(Long partnerId)
    {
        Preconditions.checkNotNull(partnerId,"合伙人id不能为空");

        List<BadBalanceByPartnerVO> badBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectBadBalance(partnerId);

        List<OverdueBalanceByPartnerVO> overdueBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectOverdueBalance(partnerId);

        List<InGuaranteeBalanceByPartnerVO> inGuaranteeBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectInGuaranteeBalance(partnerId);

        List<LoanBalanceByPartnerVO> loanBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectLoanBalance(partnerId);

        TotalLoanFinanceInfoByPartnerVO totalLoanFinanceInfoByPartnerVO =new TotalLoanFinanceInfoByPartnerVO();

        //统计不良总额
        if (badBalanceByPartnerVOS!=null && badBalanceByPartnerVOS.size()>0)
        {
            Optional<BigDecimal> totalBadBalance = badBalanceByPartnerVOS.stream()
                    .filter(badBalanceByPartnerVO -> badBalanceByPartnerVO.getBadBalance()!=null)
                    .map(badBalanceByPartnerVO -> badBalanceByPartnerVO.getBadBalance())
                    .reduce((x, y) -> x.add(y));
            if (totalBadBalance.isPresent()){
                totalLoanFinanceInfoByPartnerVO.setTotalBadBalance(totalBadBalance.get());
            }
        }

        //统计逾期总额
        if (overdueBalanceByPartnerVOS!=null && overdueBalanceByPartnerVOS.size()>0)
        {
            Optional<BigDecimal> totalOverdueBalance = overdueBalanceByPartnerVOS.stream()
                    .filter(overdueBalanceByPartnerVO -> overdueBalanceByPartnerVO.getOverdueBalance()!=null)
                    .map(badBalanceByPartnerVO -> badBalanceByPartnerVO.getOverdueBalance())
                    .reduce((x, y) -> x.add(y));
            if (totalOverdueBalance.isPresent()){
                totalLoanFinanceInfoByPartnerVO.setTotalOverdueBalance(totalOverdueBalance.get());
            }
        }

        //统计逾期总额
        if (inGuaranteeBalanceByPartnerVOS!=null && inGuaranteeBalanceByPartnerVOS.size()>0)
        {
            Optional<BigDecimal> totalInGuaranteeBalance = inGuaranteeBalanceByPartnerVOS.stream()
                    .filter(inGuaranteeBalanceByPartnerVO -> inGuaranteeBalanceByPartnerVO.getInGuaranteeBalance()!=null)
                    .map(badBalanceByPartnerVO -> badBalanceByPartnerVO.getInGuaranteeBalance())
                    .reduce((x, y) -> x.add(y));
            if (totalInGuaranteeBalance.isPresent()) {
                totalLoanFinanceInfoByPartnerVO.setTotalInGuaranteeBalance(totalInGuaranteeBalance.get());
            }
        }

        //统计贷款总额
        if (loanBalanceByPartnerVOS!=null && loanBalanceByPartnerVOS.size()>0)
        {
            Optional<BigDecimal> totalLoanBalance = loanBalanceByPartnerVOS.stream()
                    .filter(loanBalanceByPartnerVO -> loanBalanceByPartnerVO.getFinancialBankPeriodPrincipal()!=null)
                    .map(badBalanceByPartnerVO -> badBalanceByPartnerVO.getFinancialBankPeriodPrincipal())
                    .reduce((x, y) -> x.add(y));
            if (totalLoanBalance.isPresent()){
                totalLoanFinanceInfoByPartnerVO.setTotalLoanBalance(totalLoanBalance.get());
            }
        }
        //统计
        return ResultBean.ofSuccess(totalLoanFinanceInfoByPartnerVO);
    }

    @Override
    public ResultBean getOrderByCustomerId(Long customerId)
    {
        List<OrderByCustomerIdVO> list = customersLoanFinanceInfoByPartnerMapper.getOrderByCustomerId(customerId);
        //根据订单查询代偿
        if (list!=null && list.size()>0)
        {
            list.stream()
                    .forEach(orderByCustomerIdVO ->
                    {
                        if (orderByCustomerIdVO.getNum()!=null)
                        {
                            List<LoanApplyCompensationDO> loanApplyCompensationDOS = loanApplyCompensationDOMapper.selectByOrderId(orderByCustomerIdVO.getNum());
                            for (LoanApplyCompensationDO loanApplyCompensationDO :loanApplyCompensationDOS)
                            {
                                PartnerCompensations partnerCompensations =new PartnerCompensations();
                                partnerCompensations.setCompensatoryAmount(loanApplyCompensationDO.getPartnerCompensationAmount());
                                partnerCompensations.setCompensatoryTime(loanApplyCompensationDO.getPartnerDcReviewDate());
                                orderByCustomerIdVO.getPartnerCompensationsList().add(partnerCompensations);
                            }

                        }
                    });
        }
        return ResultBean.ofSuccess(list);
    }
}
