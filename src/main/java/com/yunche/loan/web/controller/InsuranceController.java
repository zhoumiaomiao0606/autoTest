package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.InsuranceUpdateParam;
import com.yunche.loan.service.InsuranceService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/loanorder/insurance")
public class InsuranceController {

    @Resource
    private InsuranceService insuranceService;
    /**
     * 车辆保险详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {

        return ResultBean.ofSuccess(insuranceService.detail(Long.valueOf(order_id)));
    }
    /**
     * 车辆保险详情
     */
    @GetMapping(value = "/query")
    public ResultBean query(@RequestParam String order_id) {

        return ResultBean.ofSuccess(insuranceService.query(Long.valueOf(order_id)));
    }
    /**
     * 录入车辆保险
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated InsuranceUpdateParam param) {
        insuranceService.update(param);
        return ResultBean.ofSuccess("保存成功");
    }

}
