package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.BankCreditChartVO;
import com.yunche.loan.domain.vo.FinancialDepartmentRemitDetailChartVO;
import com.yunche.loan.domain.vo.MortgageOverdueChartVO;
import com.yunche.loan.domain.vo.SocialCreditChartVO;
import com.yunche.loan.mapper.ChartDOMapper;
import com.yunche.loan.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-06 17:12
 * @description: 报表统一实现类
 **/
public class ChartServiceImpl implements ChartService
{
    @Autowired
    private ChartDOMapper chartDOMapper;
    @Override
    public ResultBean getSocialCreditChart(SocialCreditChartParam param)
    {

        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = chartDOMapper.selectSocialCreditChartVO(param);
        // 取分页信息
        PageInfo<SocialCreditChartVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(pageInfo);
    }

    @Override
    public ResultBean getBankCreditChart(BankCreditChartParam param) {
        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = chartDOMapper.selectBankCreditChartVO(param);
        // 取分页信息
        PageInfo<BankCreditChartVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(pageInfo);
    }

    @Override
    public ResultBean getFinancialDepartmentRemitDetailChart(FinancialDepartmentRemitDetailChartParam param) {
        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = chartDOMapper.selectFinancialDepartmentRemitDetailChartVO(param);
        // 取分页信息
        PageInfo<FinancialDepartmentRemitDetailChartVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(pageInfo);
    }

    @Override
    public ResultBean getMortgageOverdueChart(MortgageOverdueParam param) {
        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = chartDOMapper.selectMortgageOverdueChartVO(param);
        // 取分页信息
        PageInfo<MortgageOverdueChartVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(pageInfo);
    }

}
