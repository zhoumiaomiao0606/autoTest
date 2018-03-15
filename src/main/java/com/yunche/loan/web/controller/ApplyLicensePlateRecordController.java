package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApplyLicensePlateRecordUpdateParam;
import com.yunche.loan.service.ApplyLicensePlateRecordService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/loanorder/applylicenseplaterecord")
public class ApplyLicensePlateRecordController {

    @Resource
    private ApplyLicensePlateRecordService applyLicensePlateRecordService;

    /**
     * 上牌记录详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {

        return ResultBean.ofSuccess(applyLicensePlateRecordService.detail(Long.valueOf(order_id)));
    }

    /**
     * 上牌记录录入
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated ApplyLicensePlateRecordUpdateParam param) {

        applyLicensePlateRecordService.update(param);
        return ResultBean.ofSuccess("保存成功");
    }


}
