package com.yunche.loan.mapper;

import com.yunche.loan.domain.param.CreditApplyCustomerByMonthChartParam;
import com.yunche.loan.domain.param.LoanApplyOrdersByMonthChartParam;
import com.yunche.loan.domain.param.OrdersSuccessByMonthChartParam;
import com.yunche.loan.domain.vo.CreatApplyOrders;
import com.yunche.loan.domain.vo.LoanApplyOrdersVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParterChartDOMapper
{
    long selectCreditApplyCustomerCount(LoanApplyOrdersByMonthChartParam param);

    String inTheLoan(LoanApplyOrdersByMonthChartParam param);

    String alreadyMat(LoanApplyOrdersByMonthChartParam param);

    String inTheContract(LoanApplyOrdersByMonthChartParam param);

    String alreadyLoan(LoanApplyOrdersByMonthChartParam param);

    String inTheMortgage(LoanApplyOrdersByMonthChartParam param);

    String alreadyMortgage(LoanApplyOrdersByMonthChartParam param);

    List<LoanApplyOrdersVO> selectLoanApplyOrdersByMonth(LoanApplyOrdersByMonthChartParam param);

    long selectUniversalOrdersByMonthAndnode(@Param("record") String record, @Param("bytes")Byte[] bytes, @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<CreatApplyOrders> selectOrdersSuccessByMonth(OrdersSuccessByMonthChartParam param);
    //征信提交订单数
    long totalCredit(LoanApplyOrdersByMonthChartParam param);
    //主贷拒贷订单数
    long totalRefuseLend(LoanApplyOrdersByMonthChartParam param);
    //电审弃单数
    long totalTelGiveUp(LoanApplyOrdersByMonthChartParam param);
    //已垫款未抵押订单数
    long totalmakeAdvances(LoanApplyOrdersByMonthChartParam param);
    //退款未垫款订单
    long totalRefund(LoanApplyOrdersByMonthChartParam param);
    //抵押通过订单数
    long totalMortgage(LoanApplyOrdersByMonthChartParam param);
}
