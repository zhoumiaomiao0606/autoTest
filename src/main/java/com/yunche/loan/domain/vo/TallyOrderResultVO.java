package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.InsuranceRelevanceDO;
import com.yunche.loan.domain.entity.InsuranceRiskDO;
import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import lombok.Data;

import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 10:32
 * @description:
 **/
@Data
public class TallyOrderResultVO
{
    private BaseCustomerInfoVO baseCustomerInfoVO;

    private FinancialSchemeVO financialSchemeVO;

    private UniversalCostDetailsVO universalCostDetailsVO;

    private MortgageInfoVO mortgageInfoVO;

    private List<LoanApplyCompensationDO> loanApplyCompensationDOS;

    private List<InsuranceRelevanceDO> insuranceRelevanceDOS;

    private List<InsuranceRiskDO> insuranceRiskDOS;

    //

    //

    private List<UniversalCustomerVO> customers;
}
