package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanTelephoneVerifyParam;
import com.yunche.loan.service.LoanTelephoneVerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuzhe
 * @date 2018/4/12
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/telephoneVerify")
public class LoanTelephoneVerifyController {

    @Autowired
    private LoanTelephoneVerifyService loanTelephoneVerifyService;


    /**
     * 保存
     *
     * @param loanTelephoneVerifyParam
     * @return
     */
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> create(@RequestBody LoanTelephoneVerifyParam loanTelephoneVerifyParam)
    {
        return loanTelephoneVerifyService.save(loanTelephoneVerifyParam);
    }

}
