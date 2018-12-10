package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.FinancialExceptionOperationParam;
import com.yunche.loan.domain.vo.FinancialExceptionOperationVO;
import com.yunche.loan.mapper.FinancialOperationExceptionDOMapper;
import com.yunche.loan.service.FinancialExceptionOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FinancialExceptionOperationServiceImpl implements FinancialExceptionOperationService
{
    @Autowired
    private FinancialOperationExceptionDOMapper financialOperationExceptionDOMapper;
    @Override
    public ResultBean list(FinancialExceptionOperationParam financialExceptionOperationParam)
    {
        PageHelper.startPage(financialExceptionOperationParam.getPageIndex(), financialExceptionOperationParam.getPageSize(), true);

        List<FinancialExceptionOperationVO> list = financialOperationExceptionDOMapper.list(financialExceptionOperationParam);
        // 取分页信息
        PageInfo<FinancialExceptionOperationVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }
}
