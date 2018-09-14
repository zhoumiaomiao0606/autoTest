package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ConfRefundApplyAccountDO;
import com.yunche.loan.service.ConfRefundApplyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/14
 */
@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/conf/refundApplyAccount", "/api/v1/app/conf/refundApplyAccount"})
public class ConfRefundApplyAccountController {


    @Autowired
    private ConfRefundApplyAccountService confRefundApplyAccountService;


    @GetMapping("/list")
    public ResultBean<List<ConfRefundApplyAccountDO>> listAll() {
        return ResultBean.ofSuccess(confRefundApplyAccountService.listAll());
    }
}
