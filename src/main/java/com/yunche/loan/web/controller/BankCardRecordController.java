package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/finance/bankcard")
public class BankCardRecordController {


       //银行卡文件导入
       @GetMapping(value = "/imp")
        public ResultBean imp(){
            return null;
        }

        //银行卡接收单录入
        public ResultBean input(){
            return null;
        }
        //银行卡接收（针对只保存不提交的数据回显查询）
        public  ResultBean query(){
            return null;
        }

        //详情界面
        public ResultBean detail(){
            return null;
        }


}
