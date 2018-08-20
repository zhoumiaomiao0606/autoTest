package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LitigationStateDO;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.service.LawWorksService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/lawworks")
public class LawWorksController {
    @Resource
    private LawWorksService lawWorksService;

    /**
     * 法务详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id,@RequestParam String bank_repay_imp_record_id) {

        return ResultBean.ofSuccess(lawWorksService.detail(Long.valueOf(order_id),Long.valueOf(bank_repay_imp_record_id)));
    }
    /*
    *撤销诉讼
     */
    @PostMapping(value = "/revoke")
    public ResultBean litigationRevoke(@RequestBody @Validated LitigationStateDO param) {
        lawWorksService.litigationRevoke(param);
        return ResultBean.ofSuccess(null,"撤销成功");
    }


    /**
     * 诉讼录入
     */
    @PostMapping(value = "/litigationinstall", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean litigationInstall(@RequestBody @Validated LitigationParam param) {
        lawWorksService.litigationInstall(param);
        return ResultBean.ofSuccess(null,"录入成功");
    }


    /**
     * 强制执行
     */
    @PostMapping(value = "/forceinstall", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean forceInstall(@RequestBody @Validated ForceParam param) {
        lawWorksService.forceInstall(param);
        return ResultBean.ofSuccess(null,"录入成功");
    }
    /**
     * 费用登记
     */
    @PostMapping(value = "/feeinstall", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean feeInstall(@RequestBody @Validated FeeRegisterParam param) {
        lawWorksService.feeInstall(param);
        return ResultBean.ofSuccess(null,"录入成功");
    }
    /*
    *  存档信息
     */
    @PostMapping(value = "/fileinfoinstall", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean fileInfoInstall(@RequestBody @Validated FileInfoParam param) {
        lawWorksService.fileInfoInstall(param);
        return ResultBean.ofSuccess(null,"录入成功");
    }
}
