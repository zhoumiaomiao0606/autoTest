package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.BusinessReviewCalculateParam;
import com.yunche.loan.domain.param.BusinessReviewUpdateParam;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/loanorder/businessreview")
public class BusinessReviewController {

    /**
     * 业务审批单详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return null;
    }

    /**
     * 业务审批单录入
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated BusinessReviewUpdateParam param) {
        return null;
    }



    /**
     * 业务审批单计算费用明细
     */
    @PostMapping(value = "/calculate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean calculate(@RequestBody @Validated BusinessReviewCalculateParam param) {
        return null;
    }
}
