package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.BankCardRecordVO;
import com.yunche.loan.service.BankCardRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/finance/bankcard")
public class BankCardRecordController {

        @Autowired
        BankCardRecordService bankCardRecordService;

       //银行卡文件导入
        @GetMapping(value = "/imp")
        public ResultBean imp(@RequestParam("filePathName") String filePathName){
            return bankCardRecordService.importFile(filePathName);
        }

        //银行卡接收单录入
        @PostMapping(value = "/input")
        public ResultBean input(@RequestBody BankCardRecordVO bankCardRecordVO){
            return bankCardRecordService.input(bankCardRecordVO);
        }
        //银行卡接收（针对只保存不提交的数据回显查询）
        @GetMapping(value = "/querysave")
        public  ResultBean query(@RequestParam("orderId") Long orderId){
            return bankCardRecordService.query(orderId);
        }

        //详情界面
        @GetMapping(value = "/detail")
        public ResultBean detail(@RequestParam("orderId") Long orderId){
            return bankCardRecordService.detail(orderId);
        }


}
