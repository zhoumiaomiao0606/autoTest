package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.VehicleHandleUpdateParam;
import com.yunche.loan.service.VehicleHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 09:45
 * @description: 车辆处理结果详情页
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/vehicleHandle")
public class VehicleHandleController
{
    @Autowired
    private VehicleHandleService vehicleHandleService;

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String orderId) {
        return ResultBean.ofSuccess(vehicleHandleService.detail(Long.valueOf(orderId)));
    }


    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody @Validated VehicleHandleUpdateParam param) {
        vehicleHandleService.update(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }
}
