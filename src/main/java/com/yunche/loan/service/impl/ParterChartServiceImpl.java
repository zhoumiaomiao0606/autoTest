package com.yunche.loan.service.impl;

import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CreditApplyCustomerByMonthChartParam;
import com.yunche.loan.domain.param.LoanApplyOrdersByMonthChartParam;
import com.yunche.loan.domain.param.OrdersSuccessByMonthChartParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.ChartDOMapper;
import com.yunche.loan.mapper.ParterChartDOMapper;
import com.yunche.loan.service.ParterChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

import static com.yunche.loan.config.constant.LoanProcessEnum.*;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-10 11:13
 * @description: 合伙人需求报表实现
 **/
@Service
public class ParterChartServiceImpl implements ParterChartService
{
    @Autowired
    private ParterChartDOMapper parterChartDOMapper;

    @Override
    public ResultBean getCreditApplyCustomerByMonthChart(LoanApplyOrdersByMonthChartParam param)
    {
        getStartEndDate(param);
        //征信客户查询量
       long creditApplyCustomerCount =  parterChartDOMapper.selectCreditApplyCustomerCount(param);
        //征信查询结果订单量--征信查询提交
        long creditRecordCount = parterChartDOMapper.selectUniversalOrdersByMonthAndnode(BANK_CREDIT_RECORD.getCode(),new Byte[]{1,2,3},param.getStartDate(),param.getEndDate());


        //视频面签订单数
        long loanInfoRecordCount = parterChartDOMapper.selectUniversalOrdersByMonthAndnode(LOAN_INFO_RECORD.getCode(),new Byte[]{1},param.getStartDate(),param.getEndDate());

        //上门调查订单数
        long visitVerifyRecordCount = parterChartDOMapper.selectUniversalOrdersByMonthAndnode(VISIT_VERIFY.getCode(),new Byte[]{1},param.getStartDate(),param.getEndDate());
        CreditApplyCustomerCountVO creditApplyCustomerCountVO = new CreditApplyCustomerCountVO();
        creditApplyCustomerCountVO.setCreditApplyCustomerCount(creditApplyCustomerCount);
        creditApplyCustomerCountVO.setCreditRecordCount(creditRecordCount);
        creditApplyCustomerCountVO.setLoanInfoRecordCount(loanInfoRecordCount);
        creditApplyCustomerCountVO.setVisitVerifyRecordCount(visitVerifyRecordCount);

        System.out.println(creditApplyCustomerCount+"===="+creditRecordCount+"===="+loanInfoRecordCount+"===="+visitVerifyRecordCount);
        return ResultBean.ofSuccess(creditApplyCustomerCountVO);
    }
    //获取日期方法
    public void getStartEndDate(LoanApplyOrdersByMonthChartParam param) {
        int year = Integer.parseInt(param.getSelectYear());
        int month = Integer.parseInt(param.getSelectMonth());
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        String start =year+"-"+(month<10?"0"+month:""+month)+"-"+"01 00:00:00";
        String end = year+"-"+(month<10?"0"+month:""+month)+"-"+(maxDate<10?"0"+maxDate:""+maxDate)+" 23:59:59";
        param.setStartDate(start);
        param.setEndDate(end);
    }

    @Override
    public ResultBean getLoanApplyOrdersByMonthChart(LoanApplyOrdersByMonthChartParam param)
    {
        getStartEndDate(param);
        LoanApplyOrdersByMonthChartVO loanApplyOrdersByMonthChartVO =new LoanApplyOrdersByMonthChartVO();
        loanApplyOrdersByMonthChartVO.setUc_remitCount(parterChartDOMapper.inTheLoan(param));

        loanApplyOrdersByMonthChartVO.setC_remitCount(parterChartDOMapper.alreadyMat(param));

        loanApplyOrdersByMonthChartVO.setUc_materialPrintCount(parterChartDOMapper.inTheContract(param));

        loanApplyOrdersByMonthChartVO.setC_bankLendCount(parterChartDOMapper.alreadyLoan(param));

        loanApplyOrdersByMonthChartVO.setUc_depositCount(parterChartDOMapper.inTheMortgage(param));

        loanApplyOrdersByMonthChartVO.setC_depositCount(parterChartDOMapper.alreadyMortgage(param));
        return ResultBean.ofSuccess(loanApplyOrdersByMonthChartVO);
    }

    @Override
    public ResultBean getOrdersSuccessByMonthChart(LoanApplyOrdersByMonthChartParam param)
    {
        getStartEndDate(param);
        OrdersSuccessVO ordersSuccess =new OrdersSuccessVO();
        Long long1 = parterChartDOMapper.totalCredit(param);
        if(long1 == 0){
            ordersSuccess.setCreditNoPass("0");

            ordersSuccess.setRiskNoPass("0");

            ordersSuccess.setMortgageNoComplete("0");

            ordersSuccess.setRefund("0");

            ordersSuccess.setSuccess("0");

            ordersSuccess.setOther("0");
        }else{
            Long long2 = parterChartDOMapper.totalRefuseLend(param);

            Long long3 = parterChartDOMapper.totalTelGiveUp(param);

            Long long4 = parterChartDOMapper.totalmakeAdvances(param);

            Long long5 = parterChartDOMapper.totalRefund(param);

            Long long6 = parterChartDOMapper.totalMortgage(param);

            Long long7 = long1-long2-long3-long4-long5-long6;
            ordersSuccess.setCreditNoPass(long2+"");

            ordersSuccess.setRiskNoPass(long3+"");

            ordersSuccess.setMortgageNoComplete(long4+"");

            ordersSuccess.setRefund(long5+"");

            ordersSuccess.setSuccess(long6+"");

            ordersSuccess.setOther(long7+"");
            /*ordersSuccess.setCreditNoPass(String.format("%.2f", ((long2.doubleValue() / long1.doubleValue()) * 100)) + "%");

            ordersSuccess.setRiskNoPass(String.format("%.2f", ((long3.doubleValue() / long1.doubleValue()) * 100)) + "%");

            ordersSuccess.setMortgageNoComplete(String.format("%.2f", ((long4.doubleValue() / long1.doubleValue()) * 100)) + "%");

            ordersSuccess.setRefund(String.format("%.2f", ((long5.doubleValue() / long1.doubleValue()) * 100)) + "%");

            ordersSuccess.setSuccess(String.format("%.2f", ((long6.doubleValue() / long1.doubleValue()) * 100)) + "%");

            ordersSuccess.setOther(String.format("%.2f", ((long7.doubleValue() / long1.doubleValue()) * 100)) + "%");*/
        }

        return ResultBean.ofSuccess(ordersSuccess);
    }

}
