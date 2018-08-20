package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.VehicleOutboundDO;
import com.yunche.loan.domain.param.VehicleOutboundUpdateParam;
import com.yunche.loan.service.VehicleOutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 09:53
 * @description: 车辆出库详情页
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/vehicleOutbound")
public class VehicleOutboundController
{
    @Autowired
    private VehicleOutboundService vehicleOutboundService;

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String orderId,String bank_repay_imp_record_id) {
        return ResultBean.ofSuccess(vehicleOutboundService.detail(Long.valueOf(orderId),Long.valueOf(bank_repay_imp_record_id)));
    }


    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody @Validated VehicleOutboundDO param) {
        vehicleOutboundService.update(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }
}
