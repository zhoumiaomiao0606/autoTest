package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.CalcParamDo;
import com.yunche.loan.domain.param.LoanFinancialPlanParam;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface ComputeModeService {

    /**
     * 计算公式结果计算
     * @return
     */
      ResultBean<CalcParamDo> calc(@Param("id") int id, @Param("loanAmt") BigDecimal loanAmt,
                                   @Param("exeRate") BigDecimal exeRate, @Param("bankBaseRate") BigDecimal bankBaseRate,
                                   @Param("year") int year, @Param("carPrice") BigDecimal carPrice);


//    /**
//     * 计算公式结果计算
//     * @return
//     */
//    ResultBean<CalcParamDo> calc(LoanFinancialPlanParam param);




}
