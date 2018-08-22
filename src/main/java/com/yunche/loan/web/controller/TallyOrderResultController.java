package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.OrderHandleResultDO;
import com.yunche.loan.domain.param.TallyOrderResultUpdateParam;
import com.yunche.loan.service.TallyOrderResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 10:13
 * @description: 订单结清结果详情
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/tallyOrderResult")
public class TallyOrderResultController
{
    @Autowired
    private TallyOrderResultService tallyOrderResultService;

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String orderId)
    {
        return ResultBean.ofSuccess(tallyOrderResultService.detail(Long.valueOf(orderId)));
    }

    /**
     * 查询客户名称 模糊查询
     */
    @GetMapping(value = "/queryCustomerOrder")
    public ResultBean queryCustomerOrder(@RequestParam(required = false) String name)
    {
        return ResultBean.ofSuccess(tallyOrderResultService.CustomerOrder(name));
    }


    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody @Validated OrderHandleResultDO param)
    {

        return tallyOrderResultService.update(param);
    }
}
