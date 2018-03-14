package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/loanorder/universal")
public class UniversalController {

    /**
     * 客户信息
     */
    @GetMapping(value = "/customer")
    public ResultBean customer(@RequestParam String order_id) {
        return null;
    }

    /**
     * 客户附件
     */
    @GetMapping(value = "/customerfile")
    public ResultBean customerFile(@RequestParam String customer_id) {
        return null;
    }

    /**
     * 资料增补列表
     */
    @GetMapping(value = "/materialrecord")
    public ResultBean materialRecord(@RequestParam String order_id) {
        return null;
    }


}
