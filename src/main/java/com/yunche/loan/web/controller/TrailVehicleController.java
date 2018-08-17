package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.TrailVehicleUpdateParam;
import com.yunche.loan.service.TrailVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 09:28
 * @description: 上门拖车详情页
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/trailCar")
public class TrailVehicleController
{
    @Autowired
    private TrailVehicleService trailVehicleService;

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String orderId) {
        return ResultBean.ofSuccess(trailVehicleService.detail(Long.valueOf(orderId)));
    }


    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody @Validated TrailVehicleUpdateParam param) {
        trailVehicleService.update(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }

}
