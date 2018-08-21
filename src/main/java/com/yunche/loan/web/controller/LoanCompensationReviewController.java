package com.yunche.loan.web.controller;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;
import com.yunche.loan.service.LoanCompensationReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 财务代偿确认
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/compensationreview")
public class LoanCompensationReviewController {

    @Autowired
    private LoanCompensationReviewService loanCompensationReviewService;

    /**
     * 代偿确认保存
     * @param param
     * @return
     */
    @PostMapping(value = "/update",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean save(@RequestBody UniversalCompensationParam param){
        loanCompensationReviewService.save(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }

    /**
     * 详情页
     * @param query
     * @return
     */
    @PostMapping(value = "/detail",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean detail(@RequestBody UniversalCompensationQuery query){

        return loanCompensationReviewService.detail(query);

    }

}
