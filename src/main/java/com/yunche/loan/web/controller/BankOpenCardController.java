package com.yunche.loan.web.controller;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.service.BankOpenCardService;
import com.yunche.loan.service.BankSolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/opencard")
public class BankOpenCardController {
    /**
     * 1.贷款申请+上门调查之后 生成银行开卡代办
     * 2、调银行开发接口
     * 3、银行流水表记录流水
     * 4、等待银行回调，更新
     *    异常流水
     *
     *
     *
     *
     *
     *
     */

    @Autowired
    BankOpenCardService bankOpenCardService;


    @Autowired
    BankSolutionService bankSolutionService;


    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("orderId") Long orderId ) {
        Preconditions.checkNotNull(orderId,"业务单号不能为空");
        return bankOpenCardService.detail(orderId);
    }
    @PostMapping(value = "/openCard")
    public ResultBean open(@RequestBody BankOpenCardParam bankOpenCardParam){
        return bankOpenCardService.openCard(bankOpenCardParam);
    }

//    @GetMapping(value = "/importFile")
//    public ResultBean importFile(){
//        return bankOpenCardService.importFile();
//    }

}
