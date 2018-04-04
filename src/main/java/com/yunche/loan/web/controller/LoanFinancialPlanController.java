package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanFinancialPlanParam;
import com.yunche.loan.domain.vo.LoanFinancialPlanVO;
import com.yunche.loan.service.LoanFinancialPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuzhe
 * @date 2018/4/4
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/financialplan")
public class LoanFinancialPlanController {

    @Autowired
    private LoanFinancialPlanService loanFinancialPlanService;


    /**
     * 根据订单号获取贷款金融方案详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/detail")
    public ResultBean<LoanFinancialPlanVO> loanFinancialPlanDetail(@RequestParam("orderId") Long orderId) {
        return loanFinancialPlanService.loanFinancialPlanDetail(orderId);
    }

    /**
     * 金融方案计算
     *
     * @param loanFinancialPlanParam
     * @return
     */
    @PostMapping(value = "/calc", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<LoanFinancialPlanVO> calcLoanFinancialPlan(@RequestBody LoanFinancialPlanParam loanFinancialPlanParam) {
        return loanFinancialPlanService.calcLoanFinancialPlan(loanFinancialPlanParam);
    }

    /**
     * 保存贷款金融方案 【新增/编辑】
     *
     * @param loanFinancialPlanParam
     * @return
     */
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> saveLoanFinancialPlan(@RequestBody LoanFinancialPlanParam loanFinancialPlanParam) {
        return loanFinancialPlanService.createOrUpdateLoanFinancialPlan(loanFinancialPlanParam);
    }
}
