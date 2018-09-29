package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.AppBusDetailQuery;
import com.yunche.loan.domain.vo.*;

import java.util.List;

public interface AppReportService {
    ResultBean<List<AppBusinessDetailReportVO>> businessDetail(AppBusDetailQuery query);

    ResultBean<List<AppMakeMoneyDetailReportVO>> makeMoneyDetail(AppBusDetailQuery query);

    AppTableInfoVO getTableHead(String type);
}
