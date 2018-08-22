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
@RequestMapping(value = {"/api/v1/loanorder/vehicleHandle", "/api/v1/app/loanorder/vehicleHandle"})
public class VehicleHandleController
{
    @Autowired
    private VehicleHandleService vehicleHandleService;

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String orderId,Long bankRepayImpRecordId)
    {
        return ResultBean.ofSuccess(vehicleHandleService.detail(Long.valueOf(orderId),bankRepayImpRecordId));
    }


    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody @Validated VehicleHandleUpdateParam param) {
        vehicleHandleService.update(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }

    /**
     * @Author: ZhongMingxiao
     * @Param:
     * @return:
     * @Date:
     * @Description:  对接app分模块
     */
    @GetMapping(value = "/vehiclehandle")
    public ResultBean vehicleHandle(@RequestParam String orderId,String bank_repay_imp_record_id)
    {
        return ResultBean.ofSuccess(vehicleHandleService.vehicleHandle(Long.valueOf(orderId),Long.valueOf(bank_repay_imp_record_id)));
    }
}
