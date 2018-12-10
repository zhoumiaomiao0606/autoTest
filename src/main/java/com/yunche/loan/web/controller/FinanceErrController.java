package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.FinanceErrQuery;
import com.yunche.loan.service.FinanceErrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/financeerr")
public class FinanceErrController {
    @Autowired
    private FinanceErrService financeErrService;

    @PostMapping(value = "/query",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean list(@RequestBody FinanceErrQuery financeErrQuery){


        return financeErrService.query(financeErrQuery);
    }

    @GetMapping("retry")
    public ResultBean retry(){
        return financeErrService.deal();
    }
}
