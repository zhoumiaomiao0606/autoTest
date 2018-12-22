package com.yunche.loan.web.controller.ext;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.service.CustomersLoanFinanceInfoByPartnerService;
import com.yunche.loan.service.FinancialExceptionOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/api/v1/ext/financialExceptionOperation")
public class FinancialExceptionOperationController
{

    @Autowired
    private FinancialExceptionOperationService financialExceptionOperationService;

    @PostMapping("/list")
    public ResultBean list(@RequestBody FinancialExceptionOperationParam  financialExceptionOperationParam)
    {

        return financialExceptionOperationService.list(financialExceptionOperationParam);
    }

}
