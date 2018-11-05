package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateUtil;
import com.yunche.loan.domain.entity.LoanProcessLogDO;
import com.yunche.loan.domain.vo.CalcParamVO;
import com.yunche.loan.mapper.ComputeModeMapper;
import com.yunche.loan.service.ComputeModeService;
import com.yunche.loan.service.LoanProcessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

import static com.yunche.loan.config.constant.LoanProcessEnum.CREDIT_APPLY;

@Service
public class ComputeModeImpl implements ComputeModeService{

    @Autowired
    private ComputeModeMapper computeModeMapper;

    @Autowired
    private LoanProcessLogService loanProcessLogService;
    /**
     *
     *
     * @param orderId
     * @param id
     * @param loanAmt
     * @param exeRate
     * @param bankBaseRate
     * @param year
     * @param carPrice
     * @return
     */
    public ResultBean<CalcParamVO> calc(Long orderId,int id, BigDecimal loanAmt, BigDecimal exeRate, BigDecimal bankBaseRate, int year, BigDecimal carPrice){
        Preconditions.checkNotNull(id, "计算方案不能为空,请选择计算方法");
       CalcParamVO calcParamVO =   new CalcParamVO();
        HashMap paramMap = new HashMap();
        paramMap.put("loanAmt",loanAmt);
        paramMap.put("exeRate",exeRate);
        paramMap.put("bankBaseRate",bankBaseRate);
        paramMap.put("year",year);
        paramMap.put("carPrice",carPrice);
        switch (id){
            case 1:
                if(checkCreditTime(orderId)){
                    calcParamVO.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_new_1(paramMap));
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_new_1(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_new_1(paramMap));
                    calcParamVO.setLoanInterest(computeModeMapper.loanInterest_new_1(paramMap));
                    calcParamVO.setBankFee(computeModeMapper.bankFee_new_1(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_new_1(paramMap));
                    calcParamVO.setLoanToValueRatio(computeModeMapper.loanToValueRatio_new_1(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_new_1(paramMap));
                }else{
                    calcParamVO.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_1(paramMap));
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_1(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_1(paramMap));
                    calcParamVO.setLoanInterest(computeModeMapper.loanInterest_1(paramMap));
                    calcParamVO.setBankFee(computeModeMapper.bankFee_1(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_1(paramMap));
                    calcParamVO.setLoanToValueRatio(computeModeMapper.loanToValueRatio_1(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_1(paramMap));
                }

                 break;
            case 2:

                    calcParamVO.setBankPeriodPrincipal(loanAmt);
                    calcParamVO.setLoanInterest(calcParamVO.getBankPeriodPrincipal().subtract(loanAmt));
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_2(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_2(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_2(paramMap));
                    calcParamVO.setBankFee(computeModeMapper.bankFee_2(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_2(paramMap));

                break;
            case 3:
                if(checkCreditTime(orderId)){
                    calcParamVO.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_new_3(paramMap));
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_new_3(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_new_3(paramMap));
                    calcParamVO.setLoanInterest(computeModeMapper.loanInterest_new_3(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_new_3(paramMap));
                    calcParamVO.setBankFee(computeModeMapper.bankFee_new_3(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_new_3(paramMap));
                }else{
                    calcParamVO.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_3(paramMap));
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_3(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_3(paramMap));
                    calcParamVO.setLoanInterest(computeModeMapper.loanInterest_3(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_3(paramMap));
                    calcParamVO.setBankFee(computeModeMapper.bankFee_3(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_3(paramMap));
                }

                break;

            case 4:
                if(checkCreditTime(orderId)){
                    calcParamVO.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_new_4(paramMap));
                    calcParamVO.setLoanInterest(computeModeMapper.loanInterest_new_4(paramMap));
                    calcParamVO.setBankFee(computeModeMapper.bankFee_new_4(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_new_4(paramMap));
                    calcParamVO.setPrincipalFirstMonthRepay(computeModeMapper.principalFirstMonthRepay_new_4(paramMap));
                    calcParamVO.setPrincipalEachMonthRepay(computeModeMapper.principalEachMonthRepay_new_4(paramMap));
                    calcParamVO.setFirstMonthBankFee(computeModeMapper.firstMonthBankFee_new_4(paramMap));
                    calcParamVO.setEachMonthBankFee(computeModeMapper.eachMonthBankFee_new_4(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_new_4(paramMap));
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_new_4(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_new_4(paramMap));
                }else{
                    calcParamVO.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_4(paramMap));
                    calcParamVO.setLoanInterest(computeModeMapper.loanInterest_4(paramMap));
                    calcParamVO.setBankFee(computeModeMapper.bankFee_4(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_4(paramMap));
                    calcParamVO.setPrincipalFirstMonthRepay(computeModeMapper.principalFirstMonthRepay_4(paramMap));
                    calcParamVO.setPrincipalEachMonthRepay(computeModeMapper.principalEachMonthRepay_4(paramMap));
                    calcParamVO.setFirstMonthBankFee(computeModeMapper.firstMonthBankFee_4(paramMap));
                    calcParamVO.setEachMonthBankFee(computeModeMapper.eachMonthBankFee_4(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_4(paramMap));
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_4(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_4(paramMap));
                }

                break;
            case 5:

                    calcParamVO.setBankPeriodPrincipal(loanAmt);
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_5(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_5(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_5(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_5(paramMap));


                break;
            case 6:
                if(checkCreditTime(orderId)){
                    calcParamVO.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_new_6(paramMap));
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_new_6(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_new_6(paramMap));
                    calcParamVO.setLoanInterest(computeModeMapper.loanInterest_new_6(paramMap));
                    calcParamVO.setBankFee(computeModeMapper.bankFee_new_6(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_new_6(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_new_6(paramMap));
                }else{
                    calcParamVO.setBankPeriodPrincipal(computeModeMapper.periodPrincipal_6(paramMap));
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_6(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_6(paramMap));
                    calcParamVO.setLoanInterest(computeModeMapper.loanInterest_6(paramMap));
                    calcParamVO.setBankFee(computeModeMapper.bankFee_6(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_6(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_6(paramMap));
                }

                break;
            case 7:

                    calcParamVO.setBankPeriodPrincipal(loanAmt);
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_7(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_7(paramMap));
                    calcParamVO.setBankFee(computeModeMapper.bankFee_7(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_7(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_7(paramMap));
                     break;
            case 8:

                    calcParamVO.setBankPeriodPrincipal(loanAmt);
                    calcParamVO.setEachMonthRepay(computeModeMapper.eachMonthRepay_8(paramMap));
                    calcParamVO.setFirstRepayment(computeModeMapper.firstRepayment_8(paramMap));
                    calcParamVO.setBankFee(computeModeMapper.bankFee_8(paramMap));
                    calcParamVO.setTotalRepayment(computeModeMapper.totalRepayment_8(paramMap));
                    calcParamVO.setStagingRatio(computeModeMapper.stagingRatio_8(paramMap));
                    break;
        }
        //银行分期本金新公式计算
        calcParamVO.setBankPeriodPrincipalNew(computeModeMapper.periodPrincipal_new(paramMap));
        return ResultBean.ofSuccess(calcParamVO);
    }


    /**
     *
     * @param orderId
     * @return
     */
    boolean checkCreditTime(Long orderId){
        boolean flag=true;//true：新公式  false：老公式


        if(orderId==null){
            return flag;
        }
        try{
            //需要额外判断一下该订单的征信申请时间，如果是2018年11月1日之前申请的，则使用老版公式
            LoanProcessLogDO loanProcessLog = loanProcessLogService.getLoanProcessLog(orderId, CREDIT_APPLY.getCode());
            if(loanProcessLog!=null ){
                if( loanProcessLog.getCreateTime().before(DateUtil.getDate("20181101"))){
                    flag =false;
                }
            }else{
                flag =false;
            }
        }catch (Exception e){
            return false;
        }
        return flag;
    }
}
