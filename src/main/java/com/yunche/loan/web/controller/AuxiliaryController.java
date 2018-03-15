package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.InstallUpdateParam;
import com.yunche.loan.service.AuxiliaryService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/loanorder/auxiliary")
public class AuxiliaryController {
    @Resource
    private AuxiliaryService auxiliaryService;
    /**
     * 确认收到钥匙
     */
    @GetMapping(value = "/commit")
    public ResultBean commit(@RequestParam String order_id) {
        auxiliaryService.commit(Long.valueOf(order_id));
        return ResultBean.ofSuccess("确认成功");
    }

    /**
     * 安装gps
     */
    @PostMapping(value = "/install", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean install(@RequestBody @Validated InstallUpdateParam param) {
        auxiliaryService.install(param);
        return ResultBean.ofSuccess("安装成功");
    }




}
