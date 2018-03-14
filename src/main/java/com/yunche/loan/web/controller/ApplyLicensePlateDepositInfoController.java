package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApplyLicensePlateDepositInfoUpdateParam;
import com.yunche.loan.service.ApplyLicensePlateDepositInfoService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/loanorder/applylicenseplatedepositinfo")
public class ApplyLicensePlateDepositInfoController {

    @Resource
    private ApplyLicensePlateDepositInfoService applyLicensePlateDepositInfoService;


    /**
     * 上牌抵押详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return ResultBean.ofSuccess(applyLicensePlateDepositInfoService.detail(Long.valueOf(order_id)));
    }


    /**
     * 上牌抵押录入
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody @Validated ApplyLicensePlateDepositInfoUpdateParam param) {
        applyLicensePlateDepositInfoService.update(param);
        return ResultBean.ofSuccess("保存成功");
    }


}
