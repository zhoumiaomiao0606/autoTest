package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApplyLicensePlateDepositInfoUpdateParam;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/loanorder/applylicenseplatedepositinfo")
public class ApplyLicensePlateDepositInfoController {

    /**
     * 上牌抵押详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return null;
    }


    /**
     * 上牌抵押录入
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody @Validated ApplyLicensePlateDepositInfoUpdateParam param) {

        return null;
    }


}
