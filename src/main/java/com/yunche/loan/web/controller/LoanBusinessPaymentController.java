package com.yunche.loan.web.controller;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanBusinessPaymentParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.service.LoanBusinessPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/loanorder/businesspayment", "/api/v1/app/loanorder/businesspayment"})
public class LoanBusinessPaymentController {


    @Autowired
    private LoanBusinessPaymentService loanBusinessPaymentService;

    /**
     * 业务付款单
     * @param loanBusinessPaymentParam
     * @return
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody LoanBusinessPaymentParam loanBusinessPaymentParam) {
        Preconditions.checkNotNull(loanBusinessPaymentParam,"业务付款记录不能为空");
        Preconditions.checkNotNull(loanBusinessPaymentParam.getOrderId(),"订单编号不能为空");
//        Preconditions.checkNotNull(loanBusinessPaymentParam.getApplicationDate(),"申请日期不能为空");
//        Preconditions.checkNotNull(loanBusinessPaymentParam.getReceiveOpenBank(),"收款开户行不能为空");
//        Preconditions.checkNotNull(loanBusinessPaymentParam.getReceiveAccount(),"收款账户不能为空");
//        Preconditions.checkNotNull(loanBusinessPaymentParam.getAccountNumber(),"收款账号不能为空");
//        Preconditions.checkNotNull(loanBusinessPaymentParam.getPaymentOrganization(),"付款组织不能为空");
//        Preconditions.checkNotNull(loanBusinessPaymentParam.getRemark(),"备注信息不能为空");

        return loanBusinessPaymentService.save(loanBusinessPaymentParam);
    }
    /**
     * 业务付款单详情页
     * @param orderId
     * @return
     */
    @GetMapping(value = "/detail")
    public ResultBean<RecombinationVO> businessPaymentDetail(@RequestParam("orderId") Long orderId){
        return loanBusinessPaymentService.detail(orderId);
    }

    @GetMapping(value = "/appdetail")
    public ResultBean<RecombinationVO> appBusinessPaymentDetail(@RequestParam("orderId") Long orderId){
        return loanBusinessPaymentService.appDetail(orderId);
    }
}
