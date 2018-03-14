package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.InstallUpdateParam;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/loanorder/auxiliary")
public class AuxiliaryController {
    /**
     * 确认收到钥匙
     */
    @GetMapping(value = "/commit")
    public ResultBean commit(@RequestParam String order_id) {
        return null;
    }

    /**
     * 安装gps
     */
    @PostMapping(value = "/install", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean install(@RequestBody @Validated InstallUpdateParam param) {
        return null;
    }




}
