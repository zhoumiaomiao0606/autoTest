package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CreditApplyListQuery;
import com.yunche.loan.domain.param.QueryListParam;
import com.yunche.loan.domain.vo.CreditApplyListVO;
import com.yunche.loan.domain.vo.QueryListVO;

import java.util.List;

public interface OptimizationTaskschedulingService
{
    ResultBean queryCreditApplyrList(CreditApplyListQuery customerListQuery);

    ResultBean<List<QueryListVO>> queryList(QueryListParam queryListParam);
}
