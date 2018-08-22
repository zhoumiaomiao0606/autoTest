package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;
import com.yunche.loan.service.LoanPartnerCompensationReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 合伙人代偿确认
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/partnercompensationreview")
public class LoanPartnerCompensationReviewController {

    @Autowired
    LoanPartnerCompensationReviewService loanPartnerCompensationReviewService;
    //保存
    @PostMapping(value = "/save",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean save(@RequestBody UniversalCompensationParam param){
        loanPartnerCompensationReviewService.save(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }


    //详情
    @PostMapping(value = "/detail",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean detail(@RequestBody UniversalCompensationQuery query){
        return loanPartnerCompensationReviewService.detail(query);
    }

}
