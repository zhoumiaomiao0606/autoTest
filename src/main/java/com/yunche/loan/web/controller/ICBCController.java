package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ICBCApiParam;
import com.yunche.loan.service.ICBCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/icbc")
public class ICBCController {

    @Autowired
    ICBCService icbcService;
    @PostMapping (value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResultBean<Long> query( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        return icbcService.applyCredit(applyCredit);
    }
}
