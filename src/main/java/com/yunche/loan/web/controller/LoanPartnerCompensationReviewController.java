package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 合伙人代偿确认
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/partnercompensationreview")
public class LoanPartnerCompensationReviewController {


    //保存
    @PostMapping(value = "/save",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean save(@RequestBody UniversalCompensationParam param){
        return null;
    }


    //详情
    @PostMapping(value = "/detail",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean detail(@RequestBody UniversalCompensationQuery query){
        return null;
    }

}
