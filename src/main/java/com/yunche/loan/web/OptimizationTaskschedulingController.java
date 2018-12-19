package com.yunche.loan.web;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CustomerListQuery;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.CustomerListVO;
import com.yunche.loan.domain.vo.TaskListVO;
import com.yunche.loan.service.LoanCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/optTaskscheduling", "/api/v1/app/optTaskscheduling"})
public class OptimizationTaskschedulingController
{
    @Autowired
    private LoanCustomerService loanCustomerService;

    /**
     * 查询接口--列表
     */
    @Limiter(3)
    @PostMapping(value = "/queryCustomerList")
    public ResultBean<List<CustomerListVO>> scheduleCustomerList(@RequestBody @Validated CustomerListQuery customerListQuery) {
        return loanCustomerService.queryCustomerList(customerListQuery);
    }
}
