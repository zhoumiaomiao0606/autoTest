package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanInfoRegisterParam;
import com.yunche.loan.service.LoanInfoRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/loanregister")
public class LoanInfoRegisterController {

    @Autowired
    LoanInfoRegisterService loanInfoRegisterService;

    @GetMapping("/detail")
    ResultBean detail(@RequestParam("orderId") Long orderId){
        return loanInfoRegisterService.detail(orderId);
    }

    @PostMapping(value = "/update",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean update(@RequestBody LoanInfoRegisterParam loanInfoRegisterParam){
        return loanInfoRegisterService.update(loanInfoRegisterParam);
    }
}
