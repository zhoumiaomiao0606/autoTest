package com.yunche.loan.mapper;

import com.yunche.loan.domain.param.CreditApplyCustomerByMonthChartParam;
import com.yunche.loan.domain.param.LoanApplyOrdersByMonthChartParam;
import com.yunche.loan.domain.vo.LoanApplyOrdersVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ParterChartDOMapper
{
    long selectCreditApplyCustomerCount(CreditApplyCustomerByMonthChartParam param);

    List<LoanApplyOrdersVO> selectLoanApplyOrdersByMonth(LoanApplyOrdersByMonthChartParam param);
}
