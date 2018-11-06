package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.CalcParamVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface ComputeModeService {

    /**
     * 计算公式结果计算
     * @return
     */
      ResultBean<CalcParamVO> calc(@Param("orderId") Long orderId,@Param("id") int id, @Param("loanAmt") BigDecimal loanAmt,
                                   @Param("exeRate") BigDecimal exeRate, @Param("bankBaseRate") BigDecimal bankBaseRate,
                                   @Param("year") int year, @Param("carPrice") BigDecimal carPrice);


//    /**
//     * 计算公式结果计算
//     * @return
//     */
//    ResultBean<CalcParamVO> calc(LoanFinancialPlanParam param);




}
