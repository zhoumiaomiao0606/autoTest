package com.yunche.loan.web.controller;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.RiskCommitmentPara;
import com.yunche.loan.service.LoanCommitKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/11/7
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/commitKey")
public class LoanCommitKeyController {


    @Autowired
    private LoanCommitKeyService loanCommitKeyService;


    /**
     * TODO 待收钥匙新增按钮“未收，风险100%”，点击后，视同待办完成，但订单的风险承担比例改为100%
     *
     * @param orderId
     * @return
     */
    @GetMapping("/riskUncollected")
    public ResultBean<Void> riskUncollected(@RequestParam Long orderId) {
        return loanCommitKeyService.riskUncollected(orderId);
    }

    //批量不收钥匙
    @PostMapping("/batchRiskUncollected")
    public ResultBean<Void> batchRiskUncollected(@RequestBody List<Long> orderIds)
    {
        if (!CollectionUtils.isEmpty(orderIds))
        {
            orderIds
                    .stream()
                    .forEach(
                            e ->{
                                loanCommitKeyService.riskUncollected(e);
                            }
                    );
            return ResultBean.ofSuccess(null, "成功");
        }else
            {
                return ResultBean.ofSuccess(null, "订单号为空");
            }

    }

    //批量收钥匙
    @PostMapping("/batchCollected")
    public ResultBean<Void> batchCollected(@RequestBody List<Long> orderIds)
    {
        if (!CollectionUtils.isEmpty(orderIds))
        {
            orderIds
                    .stream()
                    .forEach(
                            e ->{
                                loanCommitKeyService.collected(e);
                            }
                    );
            return ResultBean.ofSuccess(null, "成功");
        }else
        {
            return ResultBean.ofSuccess(null, "订单号为空");
        }

    }

    /**
     * TODO “未收，风险不变”，点击后，视同待办完成，但订单的风险承担比例不动
     *
     * @param orderId
     * @return
     */
    @GetMapping("/uncollected")
    public ResultBean<Void> uncollected(@RequestParam Long orderId) {
        return loanCommitKeyService.uncollected(orderId);
    }

    @PostMapping("/letterOfRiskCommitment")
    public ResultBean letterOfRiskCommitment(@RequestBody RiskCommitmentPara riskCommitmentPara) {
        return loanCommitKeyService.letterOfRiskCommitment(riskCommitmentPara);
    }

    @GetMapping("/detail")
    public ResultBean detail(@RequestParam Long orderId) {
        return loanCommitKeyService.detail(orderId);
    }
}
