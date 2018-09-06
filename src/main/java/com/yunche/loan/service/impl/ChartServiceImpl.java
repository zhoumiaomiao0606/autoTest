package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.SocialCreditChartParam;
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
}
