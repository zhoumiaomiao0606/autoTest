package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.MaterialUpdateParam;
import com.yunche.loan.service.MaterialService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/material")
public class MaterialController {

    @Resource
    private MaterialService materialService;

    /**
     * 录入资料
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {


        return ResultBean.ofSuccess(materialService.detail(Long.valueOf(order_id)));
    }

    /**
     * 资料详情
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated MaterialUpdateParam param) {
        materialService.update(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }

}
