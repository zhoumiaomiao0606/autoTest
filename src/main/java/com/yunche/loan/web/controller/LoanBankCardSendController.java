package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanBankCardSendDO;
import com.yunche.loan.domain.vo.UniversalBankCardSendVO;
import com.yunche.loan.service.LoanBankCardSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/loanBankCardSend", "/api/v1/app/loanBankCardSend"})
public class LoanBankCardSendController {

    @Autowired
    private LoanBankCardSendService loanBankCardSendService;


    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> save(@RequestBody LoanBankCardSendDO loanBankCardSendDO) {
        return loanBankCardSendService.save(loanBankCardSendDO);
    }

    @GetMapping(value = "/detail")
    public ResultBean<UniversalBankCardSendVO> detail(@RequestParam Long orderId) {
        return loanBankCardSendService.detail(orderId);
    }

    @GetMapping(value = "/imp")
    public ResultBean<Integer> imp(@RequestParam(value = "key") String ossKey) {
        return loanBankCardSendService.imp(ossKey);
    }
}

