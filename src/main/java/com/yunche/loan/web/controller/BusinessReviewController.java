package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.BusinessReviewCalculateParam;
import com.yunche.loan.domain.param.BusinessReviewUpdateParam;
import com.yunche.loan.service.BusinessReviewService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/businessreview")
public class BusinessReviewController {
    @Resource
    private BusinessReviewService businessReviewService;

    /**
     * 业务审批单详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return ResultBean.ofSuccess(businessReviewService.detail(Long.valueOf(order_id)));

    }

    /**
     * 业务审批单录入
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated BusinessReviewUpdateParam param) {
        businessReviewService.update(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }



    /**
     * 业务审批单计算费用明细
     */
    @PostMapping(value = "/calculate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean calculate(@RequestBody @Validated BusinessReviewCalculateParam param) {
        return ResultBean.ofSuccess(businessReviewService.calculate(param));
    }
}
