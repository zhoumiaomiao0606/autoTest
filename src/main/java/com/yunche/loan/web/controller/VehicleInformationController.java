package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.VehicleInformationUpdateParam;
import com.yunche.loan.service.VehicleInformationService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/vehicleinformation")
public class VehicleInformationController {

    @Resource
    private VehicleInformationService vehicleInformationService;

    /**
     * 提车资料详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id){
        return ResultBean.ofSuccess(vehicleInformationService.detail(Long.valueOf(order_id)));
    }


    /**
     * 提车资料录入
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody @Validated VehicleInformationUpdateParam param) {
        vehicleInformationService.update(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }
}
