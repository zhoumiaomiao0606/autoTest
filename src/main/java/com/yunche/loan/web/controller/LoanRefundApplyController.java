package com.yunche.loan.web.controller;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanRefundApplyParam;
import com.yunche.loan.service.LoanRefundApplyService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/loanrefundapply", "/api/v1/app/loanrefundapply"})
public class LoanRefundApplyController {

    @Resource
    private LoanRefundApplyService loanRefundApplyService;

    /**
     * 提车资料详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam Long order_id,
                             @RequestParam(required = false) Long refund_id) {

        return ResultBean.ofSuccess(loanRefundApplyService.detail(order_id, refund_id));
    }

    /**
     * 退款单更新 -save(创建/更新)
     */
    @Limiter
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> update(@RequestBody @Validated LoanRefundApplyParam param) {
        return loanRefundApplyService.update(param);
    }

    /**
     * 查询客户名称 模糊查询
     */
    @GetMapping(value = "/queryRefundCustomerOrder")
    public ResultBean queryRefundCustomerOrder(@RequestParam(required = false) String name) {
        return ResultBean.ofSuccess(loanRefundApplyService.queryRefundCustomerOrder(name));
    }
}
