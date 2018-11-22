package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.FinanceErrQuery;

public interface FinanceErrService {

    ResultBean query(FinanceErrQuery financeErrQuery);//查询

    ResultBean deal();//处理当前所有的异常订单
}
