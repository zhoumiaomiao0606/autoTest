package com.yunche.loan.service.impl;

import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CreditApplyCustomerByMonthChartParam;
import com.yunche.loan.domain.param.LoanApplyOrdersByMonthChartParam;
import com.yunche.loan.domain.param.LoanApplyOrdersByMonthRemitDetailChartParam;
import com.yunche.loan.domain.vo.LoanApplyOrdersByMonthChartVO;
import com.yunche.loan.domain.vo.LoanApplyOrdersVO;
import com.yunche.loan.mapper.ChartDOMapper;
import com.yunche.loan.mapper.ParterChartDOMapper;
import com.yunche.loan.service.ParterChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public ResultBean getCreditApplyCustomerByMonthChart(CreditApplyCustomerByMonthChartParam param)
    {

        //征信客户查询量
       long creditApplyCustomerCount =  parterChartDOMapper.selectCreditApplyCustomerCount(param);
        //征信查询订单量

        //视频面签订单数

        //上门调查订单数
     /*   parterChartDOMapper.select*/
        return null;
    }

    @Override
    public ResultBean getLoanApplyOrdersByMonthChart(LoanApplyOrdersByMonthChartParam param)
    {
        LoanApplyOrdersByMonthChartVO loanApplyOrdersByMonthChartVO =new LoanApplyOrdersByMonthChartVO();
        //获取该月提交贷款申请的所有订单，及该月该订单执行的操作及操作时间
        List<LoanApplyOrdersVO> loanApplyOrders = parterChartDOMapper.selectLoanApplyOrdersByMonth(param);
        //提取出订单总数
        long totalOrders = loanApplyOrders.stream()
                .map(LoanApplyOrdersVO::getOrderId)
                .distinct()
                .count();
        //筛选已垫款的订单数
        long c_remitCount = loanApplyOrders.stream()
                .filter(loanApplyOrdersVO -> loanApplyOrdersVO.getTask_definition_key().equals(REMIT_REVIEW.getName()))
                .map(LoanApplyOrdersVO::getOrderId)
                .distinct()
                .count();
        loanApplyOrdersByMonthChartVO.setUc_remitCount(totalOrders-c_remitCount);
        loanApplyOrdersByMonthChartVO.setUc_remitCount(c_remitCount);

        //筛选合同套打未完成的订单数
        long c_materialPrintCount = loanApplyOrders.stream()
                .filter(loanApplyOrdersVO -> loanApplyOrdersVO.getTask_definition_key().equals(MATERIAL_PRINT_REVIEW.getName()))
                .map(LoanApplyOrdersVO::getOrderId)
                .distinct()
                .count();
        loanApplyOrdersByMonthChartVO.setUc_materialPrintCount(totalOrders-c_materialPrintCount);

        //筛选银行放款的订单数
        long c_bankLendCount = loanApplyOrders.stream()
                .filter(loanApplyOrdersVO -> loanApplyOrdersVO.getTask_definition_key().equals(BANK_LEND_RECORD.getName()))
                .map(LoanApplyOrdersVO::getOrderId)
                .distinct()
                .count();
        loanApplyOrdersByMonthChartVO.setC_bankLendCount(c_bankLendCount);
        //筛选车辆抵押完成的订单数
        long c_depositCount = loanApplyOrders.stream()
                .filter(loanApplyOrdersVO -> loanApplyOrdersVO.getTask_definition_key().equals(APPLY_LICENSE_PLATE_DEPOSIT_INFO.getName()))
                .map(LoanApplyOrdersVO::getOrderId)
                .distinct()
                .count();
        loanApplyOrdersByMonthChartVO.setUc_depositCount(totalOrders-c_depositCount);
        loanApplyOrdersByMonthChartVO.setC_depositCount(c_depositCount);


        return ResultBean.ofSuccess(loanApplyOrdersByMonthChartVO);
    }

    @Override
    public ResultBean getLoanApplyOrdersByMonthRemitDetailChart(LoanApplyOrdersByMonthRemitDetailChartParam param) {
        return null;
    }
}
