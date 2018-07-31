package com.yunche.loan.web.controller;


import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.TelephoneVerifyParam;
import com.yunche.loan.service.TelephoneVerifyService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/telephoneverify"})
public class TelephoneVerifyController {

    @Resource
    private TelephoneVerifyService telephoneVerifyService;

    /**
     * 待办任务列表
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return ResultBean.ofSuccess(telephoneVerifyService.detail(Long.valueOf(order_id)));
    }

    /**
     * 更新
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated TelephoneVerifyParam param) {
        telephoneVerifyService.update(param);
        return ResultBean.ofSuccess(null, "保存成功");
    }

    /**
     * EXCEl导出
     */
    @PostMapping(value = "/export")
    public ResultBean export(@RequestBody TelephoneVerifyParam telephoneVerifyParam) {
        Preconditions.checkNotNull(telephoneVerifyParam.getStartDate(), "开始时间不能为空");
        Preconditions.checkNotNull(telephoneVerifyParam.getEndDate(), "结束时间不能为空");
        return ResultBean.ofSuccess(telephoneVerifyService.export(telephoneVerifyParam));
    }

}


