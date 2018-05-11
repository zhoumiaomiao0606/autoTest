package com.yunche.loan.web.controller;


import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.BankRepayRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/repaymentRecord")
public class BankRepayRecordController {

    @Autowired
    BankRepayRecordService bankRepayRecordService;

    @GetMapping(value = "/list")
    public ResultBean query(@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
       return bankRepayRecordService.batchFileList(pageIndex,pageSize);
    }

    @GetMapping(value = "/imp")
    public ResultBean importFile(@RequestParam("key") String ossKey){
      return bankRepayRecordService.importFile(ossKey);

    }

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("orderId") Long orderId ) {
        Preconditions.checkNotNull(orderId,"业务单号不能为空");
        return bankRepayRecordService.detail(orderId);
    }




}
