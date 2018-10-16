package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.BusinessReviewCalculateParam;
import com.yunche.loan.domain.param.BusinessReviewUpdateParam;
import com.yunche.loan.domain.param.ParternerRuleParam;
import com.yunche.loan.domain.param.ParternerRuleSharpTuningeParam;
import com.yunche.loan.manager.finance.BusinessReviewManager;
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

    @Resource
    private BusinessReviewManager businessReviewManager;

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

    /**
     * 财务系统对接-----页面渲染--初始计算
     */
    @PostMapping(value = "/parternerRule", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String parternerRule(@RequestBody @Validated ParternerRuleParam param) {
        return businessReviewManager.financeUnisal(param,"/costcalculation");
    }

    /**
     * 财务系统对接-----微调参数--重新计算
     */
    @PostMapping(value = "/parternerRuleSharpTuning", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String parternerRuleSharpTuning(@RequestBody @Validated ParternerRuleSharpTuningeParam param)
    {
        System.out.println("==================");
        return businessReviewService.parternerRuleSharpTuning(param);
        //return businessReviewManager.financeUnisal(param,"/costcalculation/detailn");
    }
}
