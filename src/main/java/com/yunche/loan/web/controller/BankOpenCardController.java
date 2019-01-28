package com.yunche.loan.web.controller;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.BankOpenCardExportParam;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.service.BankOpenCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/opencard")
public class BankOpenCardController {

    @Autowired
    BankOpenCardService bankOpenCardService;

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("orderId") Long orderId ) {

        Preconditions.checkNotNull(orderId,"业务单号不能为空");

        return bankOpenCardService.detail(orderId);
    }
    @GetMapping(value = "/openCard")
    public ResultBean open(@RequestParam("orderId") Long orderId){

        return bankOpenCardService.openCard(orderId);
    }

    @PostMapping(value = "/save")
    public ResultBean save(@RequestBody BankOpenCardParam bankOpenCardParam){

        return bankOpenCardService.save(bankOpenCardParam);
    }

    @GetMapping(value = "/taskStatus")
    public ResultBean taskschedule(@RequestParam("orderId") Long orderId){

        return  bankOpenCardService.taskschedule(orderId);
    }

    @PostMapping(value = "/export")
    public ResultBean export(@RequestBody BankOpenCardExportParam bankOpenCardExportParam){

        return bankOpenCardService.export(bankOpenCardExportParam);
    }
}
