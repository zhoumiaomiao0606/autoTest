package com.yunche.loan.web.controller;

import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ICBCApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/icbc")
public class ICBCController {

    @Autowired
    private ICBCFeignClient icbcFeignClient;

    @PostMapping (value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResultBean<Long> query( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        return icbcFeignClient.applyCredit(applyCredit);
    }


    @PostMapping (value = "/creditresult", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditresult( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        return icbcFeignClient.applyCredit(applyCredit);
    }


    @PostMapping (value = "/creditreturn", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditreturn( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        return icbcFeignClient.applyCredit(applyCredit);
    }

    @PostMapping (value = "/creditcardresult", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditcardresult( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        return icbcFeignClient.applyCredit(applyCredit);
    }

    @PostMapping (value = "/fileNotice", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean fileNotice( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        return icbcFeignClient.applyCredit(applyCredit);
    }
}
