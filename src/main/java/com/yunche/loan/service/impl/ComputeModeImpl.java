package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.CalcParamDo;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.ComputeModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

@Service
public class ComputeModeImpl implements ComputeModeService{

    @Autowired
    private ComputeModeMapper computeModeMapper;
    /**
     *
     * @param id
     * @param loanAmt
     * @param exeRate
     * @param bankBaseRate
     * @param year
     * @param carPrice
     * @return
     */
    public ResultBean<CalcParamDo> calc(int id, BigDecimal loanAmt, BigDecimal exeRate, BigDecimal bankBaseRate, int year, BigDecimal carPrice){
        Preconditions.checkNotNull(id, "计算方案不能为空,请选择计算方法");
       CalcParamDo calcParamDo =   new CalcParamDo();
        HashMap paramMap = new HashMap();
        paramMap.put("loanAmt",loanAmt);
        paramMap.put("exeRate",exeRate);
        paramMap.put("bankBaseRate",bankBaseRate);
        paramMap.put("year",year);
        paramMap.put("carPrice",carPrice);
        switch (id){
            case 1:calcParamDo.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_1(paramMap));
                 calcParamDo.setEachMonthRepay(computeModeMapper.eachMonthRepay_1(paramMap));
                 calcParamDo.setFirstRepayment(computeModeMapper.firstRepayment_1(paramMap));
                 calcParamDo.setLoanInterest(computeModeMapper.loanInterest_1(paramMap));
                 calcParamDo.setBankFee(computeModeMapper.bankFee_1(paramMap));
                 calcParamDo.setTotalRepayment(computeModeMapper.totalRepayment_1(paramMap));
                 calcParamDo.setLoanToValueRatio(computeModeMapper.loanToValueRatio_1(paramMap));
                 calcParamDo.setStagingRatio(computeModeMapper.stagingRatio_1(paramMap));
                 break;
            case 2:calcParamDo.setBankPeriodPrincipal(loanAmt);
                calcParamDo.setEachMonthRepay(computeModeMapper.eachMonthRepay_2(paramMap));
                calcParamDo.setFirstRepayment(computeModeMapper.firstRepayment_2(paramMap));
                calcParamDo.setStagingRatio(computeModeMapper.stagingRatio_2(paramMap));
                calcParamDo.setBankFee(computeModeMapper.bankFee_2(paramMap));
                break;
            case 3:calcParamDo.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_3(paramMap));
                calcParamDo.setEachMonthRepay(computeModeMapper.eachMonthRepay_3(paramMap));
                calcParamDo.setFirstRepayment(computeModeMapper.firstRepayment_3(paramMap));
                calcParamDo.setLoanInterest(computeModeMapper.loanInterest_3(paramMap));
                calcParamDo.setStagingRatio(computeModeMapper.stagingRatio_3(paramMap));
                calcParamDo.setBankFee(computeModeMapper.bankFee_3(paramMap));
                break;

            case 4:calcParamDo.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_4(paramMap));
                calcParamDo.setLoanInterest(computeModeMapper.loanInterest_4(paramMap));
                calcParamDo.setBankFee(computeModeMapper.bankFee_4(paramMap));
                calcParamDo.setTotalRepayment(computeModeMapper.totalRepayment_4(paramMap));
                calcParamDo.setPrincipalFirstMonthRepay(computeModeMapper.principalFirstMonthRepay_4(paramMap));
                calcParamDo.setPrincipalEachMonthRepay(computeModeMapper.principalEachMonthRepay_4(paramMap));
                calcParamDo.setFirstMonthBankFee(computeModeMapper.firstMonthBankFee_4(paramMap));
                calcParamDo.setEachMonthBankFee(computeModeMapper.eachMonthBankFee_4(paramMap));
                calcParamDo.setFirstRepayment(computeModeMapper.firstRepayment_4(paramMap));
                calcParamDo.setEachMonthRepay(computeModeMapper.eachMonthRepay_4(paramMap));
                calcParamDo.setStagingRatio(computeModeMapper.stagingRatio_4(paramMap));
                break;
            case 5:calcParamDo.setBankPeriodPrincipal(loanAmt);
                calcParamDo.setEachMonthRepay(computeModeMapper.eachMonthRepay_5(paramMap));
                calcParamDo.setFirstRepayment(computeModeMapper.firstRepayment_5(paramMap));
                calcParamDo.setStagingRatio(computeModeMapper.stagingRatio_5(paramMap));
                break;
            case 6:calcParamDo.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_6(paramMap));
                calcParamDo.setEachMonthRepay(computeModeMapper.eachMonthRepay_6(paramMap));
                calcParamDo.setFirstRepayment(computeModeMapper.firstRepayment_6(paramMap));
                calcParamDo.setLoanInterest(computeModeMapper.loanInterest_6(paramMap));
                calcParamDo.setBankFee(computeModeMapper.bankFee_6(paramMap));
                calcParamDo.setTotalRepayment(computeModeMapper.totalRepayment_6(paramMap));
                calcParamDo.setStagingRatio(computeModeMapper.stagingRatio_6(paramMap));
                break;
            case 7:calcParamDo.setBankPeriodPrincipal(loanAmt);
                calcParamDo.setEachMonthRepay(computeModeMapper.eachMonthRepay_7(paramMap));
                calcParamDo.setFirstRepayment(computeModeMapper.firstRepayment_7(paramMap));
                calcParamDo.setBankFee(computeModeMapper.bankFee_7(paramMap));
                calcParamDo.setTotalRepayment(computeModeMapper.totalRepayment_7(paramMap));
                calcParamDo.setStagingRatio(computeModeMapper.stagingRatio_7(paramMap));
                break;
            case 8:calcParamDo.setBankPeriodPrincipal(loanAmt);
                calcParamDo.setEachMonthRepay(computeModeMapper.eachMonthRepay_8(paramMap));
                calcParamDo.setFirstRepayment(computeModeMapper.firstRepayment_8(paramMap));
                calcParamDo.setBankFee(computeModeMapper.bankFee_8(paramMap));
                calcParamDo.setTotalRepayment(computeModeMapper.totalRepayment_8(paramMap));
                calcParamDo.setStagingRatio(computeModeMapper.stagingRatio_8(paramMap));
            break;
        }

        return ResultBean.ofSuccess(calcParamDo);
    }
}
