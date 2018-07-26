package com.yunche.loan.web.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 催收分配工作台
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/insuranceurge")
public class InsuranceUrgeDistributeController {
    @GetMapping("/tasklist")
    public void list(){

    }


}
