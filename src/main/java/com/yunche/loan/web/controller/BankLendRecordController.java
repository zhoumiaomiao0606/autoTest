package com.yunche.loan.web.controller;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.BankLendRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/finance/banklend")
public class BankLendRecordController {


    @Autowired
    BankLendRecordService bankLendRecordService;

//    @GetMapping(value = "/query")
//    public ResultBean query() {
//        //TODO 查询
//        return null;
//    }


    @GetMapping(value = "/imp")
    public ResultBean importFile(@RequestParam("filePathName") String filePathName){
        //TODO


        return null;

    }
    @GetMapping(value = "/input")
    public ResultBean manualInput(@RequestParam("filePathName") String filePathName){
        //TODO
        return null;

    }


    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("orderId") Long orderId ) {
        Preconditions.checkNotNull(orderId,"业务单号不能为空");
        return ResultBean.ofSuccess(bankLendRecordService.detail(orderId));
    }




}
