package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;
import com.yunche.loan.service.LoanApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/18
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/loanApply")
public class LoanApplyController {

    @Autowired
    private LoanApplyService loanApplyService;


    /**
     * 客户名称模糊搜索
     *
     * @param name
     * @return
     */
    @GetMapping(value = "/queryLoanApplyCustomerOrder")
    public ResultBean<List<UniversalCustomerOrderVO>> queryLoanApplyCustomerOrder(@RequestParam(required = false) String name) {
        return ResultBean.ofSuccess(loanApplyService.queryLoanApplyCustomerOrder(name));
    }
}
