package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanInfoRegisterParam;

public interface LoanInfoRegisterService {

     ResultBean detail(Long orderId);

     ResultBean update(LoanInfoRegisterParam loanInfoRegisterParam);


}
