package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.AppBusDetailQuery;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.vo.AppBusinessDetailReportVO;
import com.yunche.loan.domain.vo.AppMakeMoneyDetailReportVO;
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

    //未抵押客户

    //抵押和资料超期数

    //牌证时效考核

    //资料时效考核

    @GetMapping("/gettablehead")
    public ResultBean getTableHead(@RequestParam String type){
        return ResultBean.ofSuccess(appReportService.getTableHead(type));
    }


}
