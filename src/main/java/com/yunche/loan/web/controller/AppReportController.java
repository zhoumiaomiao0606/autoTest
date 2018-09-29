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

    @PostMapping(value = "/businessdetail",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppBusinessDetailReportVO>> businessDetail(@RequestBody AppBusDetailQuery query) {

        return appReportService.businessDetail(query);
    }
    @PostMapping(value = "/makemoneydetail",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppMakeMoneyDetailReportVO>> makeMoneyDetail(@RequestBody AppBusDetailQuery query) {

        return appReportService.makeMoneyDetail(query);
    }

    @GetMapping("/gettablehead")
    public ResultBean getTableHead(@RequestParam String type){
        return ResultBean.ofSuccess(appReportService.getTableHead(type));
    }


}
