package com.yunche.loan.web.controller;


import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.RepaymentRecordDOKey;
import com.yunche.loan.service.RepaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/repaymentRecord")
public class RepaymentRecordController {

    @Autowired
    RepaymentRecordService repaymentRecordService;

    @GetMapping(value = "/query")
    public ResultBean query() {
        return repaymentRecordService.query();
    }

    @GetMapping(value = "/imp")
    public ResultBean importFile(@RequestParam("filePathName") String filePathName){

      return repaymentRecordService.importFile(filePathName);

    }

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("orderId") Long orderId ) {
        Preconditions.checkNotNull(orderId,"业务单号不能为空");
        return repaymentRecordService.detail(orderId);
    }


}
