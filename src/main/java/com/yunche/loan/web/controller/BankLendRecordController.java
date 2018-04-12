package com.yunche.loan.web.controller;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.OSSFileParam;
import com.yunche.loan.domain.vo.BankLendRecordVO;
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


    @PostMapping(value = "/imp")
    public ResultBean importFile(@RequestBody OSSFileParam ossFileParam){
        Preconditions.checkNotNull(ossFileParam.getKey(),"文件名不能为空");
        return  bankLendRecordService.importFile(ossFileParam.getKey());

    }
    @PostMapping(value = "/input")
    public ResultBean manualInput(@RequestBody BankLendRecordVO bankLendRecordVO){
        Preconditions.checkNotNull(bankLendRecordVO,"银行放款记录不能为空");
        return bankLendRecordService.manualInput(bankLendRecordVO);

    }

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("orderId") Long orderId ) {
        Preconditions.checkNotNull(orderId,"业务单号不能为空");
        return bankLendRecordService.detail(orderId);
    }


    @GetMapping(value = "/querysave")
    public ResultBean querySave(@RequestParam("orderId") Long orderId ){
        Preconditions.checkNotNull(orderId,"业务单号不能为空");
        return bankLendRecordService.querySave(orderId);

    }

}
