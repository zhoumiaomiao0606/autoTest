package com.yunche.loan.web.controller;

import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ICBCApiCallbackParam;
import com.yunche.loan.domain.param.ICBCApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/icbc")
public class ICBCController {

    @Autowired
    private ICBCFeignClient icbcFeignClient;

    @PostMapping (value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResultBean<Long> query( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        //return icbcFeignClient.applyCredit(applyCredit);
        return null;
    }


    @PostMapping (value = "/creditresult", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void creditresult(@Validated @NotNull @RequestBody ICBCApiCallbackParam.Callback callback) {

    }


    @PostMapping (value = "/creditreturn", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditreturn( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        return null;    }

    @PostMapping (value = "/creditcardresult", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditcardresult( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        return null;    }

    @PostMapping (value = "/fileNotice", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean fileNotice( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        return null;    }
}
