package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;
import com.yunche.loan.service.LoanPartnerCompensationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 合伙人代偿
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/partnercompensation")
public class LoanPartnerCompensationController {


    @Autowired
    private LoanPartnerCompensationService loanPartnerCompensationService;

    /**
     * 合伙人代偿保存
     *
     * @param universalCompensationParam
     * @return
     */
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean save(@RequestBody UniversalCompensationParam universalCompensationParam) {

        loanPartnerCompensationService.save(universalCompensationParam);
        return ResultBean.ofSuccess(null, "保存成功");
    }


    /**
     * 详情页
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/detail", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean detail(@RequestBody UniversalCompensationQuery query) {

        return loanPartnerCompensationService.detail(query);
    }
}
