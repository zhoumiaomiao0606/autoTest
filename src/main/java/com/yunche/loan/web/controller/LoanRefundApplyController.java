package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanRefundApplyParam;
import com.yunche.loan.service.LoanRefundApplyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanrefundapply")
public class LoanRefundApplyController {

    @Resource
    private LoanRefundApplyService loanRefundApplyService;

    /**
     * 提车资料详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id,@RequestParam(required = false) String refund_id){

        return ResultBean.ofSuccess(loanRefundApplyService.detail(Long.valueOf(order_id),StringUtils.isBlank(refund_id)?null:Long.valueOf(refund_id)));
    }


    /**
     * 退款单更新
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated LoanRefundApplyParam param) {
        loanRefundApplyService.update(param);
        return ResultBean.ofSuccess(null);
    }

    /**
     *查询客户名称 模糊查询
     */
    @GetMapping(value = "/queryRefundCustomerOrder")
    public ResultBean queryRefundCustomerOrder(@RequestParam(required = false) String name) {
        return ResultBean.ofSuccess(loanRefundApplyService.queryRefundCustomerOrder(name));
    }
}
