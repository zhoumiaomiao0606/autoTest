package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.OrderHandleResultDO;
import com.yunche.loan.domain.entity.OverdueInterestDO;
import com.yunche.loan.domain.param.ContractOverDueParam;
import com.yunche.loan.service.ContractOverDueService;
import com.yunche.loan.service.TallyOrderResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 10:13
 * @description: 合同超期
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/contractOverDue")
public class ContractOverDueController
{
    @Autowired
    private ContractOverDueService contractOverDueService;


    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean list(@RequestBody @Validated ContractOverDueParam param)
    {

        return contractOverDueService.list(param);
    }

    /**
     * 导出
     */
    @PostMapping(value = "/exportContractOverDue")
    public ResultBean exportContractOverDue(@RequestBody ContractOverDueParam param)
    {
        return ResultBean.ofSuccess(contractOverDueService.exportContractOverDue(param));
    }
    /**
     * 详情页
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam Long orderId)
    {
        return ResultBean.ofSuccess(contractOverDueService.detail(orderId));
    }

    /**
     * 详情页
     */
    @GetMapping(value = "/interest")
    public ResultBean interestDetail(@RequestParam Long orderId)
    {
        return ResultBean.ofSuccess(contractOverDueService.interestDetail(orderId));
    }

    /**
     * 详情页
     */
    @GetMapping(value = "/interestUpdate")
    public ResultBean update(@RequestBody @Validated OverdueInterestDO overdueInterestDO)
    {
        contractOverDueService.update(overdueInterestDO);

        return ResultBean.ofSuccess(null,"保存成功");
    }

}
