package com.yunche.loan.service;

import com.github.pagehelper.PageInfo;
import com.yunche.loan.domain.param.CreateExpensesDetailParam;
import com.yunche.loan.domain.param.LegworkReimbursementParam;
import com.yunche.loan.domain.param.SubimitVisitDoorParam;
import com.yunche.loan.domain.vo.LegworkReimbursementUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;

import java.util.List;

public interface LegworkReimbursementService {

    public PageInfo subimitVisitDoorList(SubimitVisitDoorParam param);

    public PageInfo list(LegworkReimbursementParam param);

    public Long createExpensesDetail(CreateExpensesDetailParam param);

    RecombinationVO expensesDetail(Long id);

    void expensesUpdate(LegworkReimbursementUpdateParam param);
}
