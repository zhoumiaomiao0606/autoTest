package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.LoanCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/calculator")
public class LoanCalculatorController {

    @Autowired
    private LoanCalculatorService loanCalculatorService;

    @GetMapping("/product")
    public ResultBean getAllProduct(){
        return loanCalculatorService.getAllProduct();
    }

    @GetMapping("/cal")
    public ResultBean cal(@RequestParam("prodId") Long prodId,@RequestParam("loanAmt")BigDecimal loanAmt,
                           @RequestParam("exeRate") BigDecimal exeRate,@RequestParam("loanTime") int loanTime,
                           @RequestParam("carPrice") BigDecimal carPrice){
        return loanCalculatorService.cal(prodId,loanAmt,exeRate,loanTime,carPrice);
    }
}

