package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.AppBusDetailQuery;
import com.yunche.loan.domain.vo.*;

import java.util.List;

public interface AppReportService {
    ResultBean<List<AppBusinessDetailReportVO>> businessDetail(AppBusDetailQuery query);

    ResultBean<List<AppMakeMoneyDetailReportVO>> makeMoneyDetail(AppBusDetailQuery query);

    ResultBean<List<AppBussinessRankReportVO>> businessRank(AppBusDetailQuery query);

    ResultBean<List<AppNoMortgageCusReportVO>> noMortgageCus(AppBusDetailQuery query);

    ResultBean<List<AppMortgageAndDataOverdueReportVO>> mortgageAndDataOverdue(AppBusDetailQuery query);

    ResultBean<List<AppCardsTimeCheckReportVO>> cardsTimeCheck(AppBusDetailQuery query);

    ResultBean<List<AppDataTimeCheckReportVO>> dataTimeCheck(AppBusDetailQuery query);

    AppTableInfoVO getTableHead(String type);
}
