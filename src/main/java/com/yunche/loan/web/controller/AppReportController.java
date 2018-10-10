package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.AppBusDetailQuery;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.AppReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/app/appreport")
public class AppReportController {
    @Autowired
    private AppReportService appReportService;
    //业务明细表
    @PostMapping(value = "/businessdetail",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppBusinessDetailReportVO>> businessDetail(@RequestBody AppBusDetailQuery query) {

        return appReportService.businessDetail(query);
    }
    //垫款明细表
    @PostMapping(value = "/makemoneydetail",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppMakeMoneyDetailReportVO>> makeMoneyDetail(@RequestBody AppBusDetailQuery query) {

        return appReportService.makeMoneyDetail(query);
    }
    //业务量排行
    @PostMapping(value = "/businessrank",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppBussinessRankReportVO>> businessRank(@RequestBody AppBusDetailQuery query) {
        return appReportService.businessRank(query);
    }
    //未抵押客户
    @PostMapping(value = "/nomortgagecus",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppNoMortgageCusReportVO>> noMortgageCus(@RequestBody AppBusDetailQuery query) {
        return appReportService.noMortgageCus(query);
    }
    //抵押和资料超期数
    @PostMapping(value = "/mortgageanddataoverdue",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppMortgageAndDataOverdueReportVO>> mortgageAndDataOverdue(@RequestBody AppBusDetailQuery query) {
        return appReportService.mortgageAndDataOverdue(query);
    }
    //牌证时效考核
    @PostMapping(value = "/cardstimecheck",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppCardsTimeCheckReportVO>> cardsTimeCheck(@RequestBody AppBusDetailQuery query) {
        return appReportService.cardsTimeCheck(query);
    }
    //资料时效考核
    @PostMapping(value = "/datatimecheck",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppDataTimeCheckReportVO>> dataTimeCheck(@RequestBody AppBusDetailQuery query) {
        return appReportService.dataTimeCheck(query);
    }


    @GetMapping("/gettablehead")
    public ResultBean getTableHead(@RequestParam("type") String type){
        return ResultBean.ofSuccess(appReportService.getTableHead(type));
    }


}
