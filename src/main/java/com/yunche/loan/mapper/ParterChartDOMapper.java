package com.yunche.loan.mapper;

import com.yunche.loan.domain.param.CreditApplyCustomerByMonthChartParam;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParterChartDOMapper {
    long selectCreditApplyCustomerCount(CreditApplyCustomerByMonthChartParam param);
}
