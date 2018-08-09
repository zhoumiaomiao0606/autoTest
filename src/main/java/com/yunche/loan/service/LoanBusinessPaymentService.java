package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanBusinessPaymentParam;

public interface LoanBusinessPaymentService {

    /**
     * 业务申请单保存
     * @param loanBusinessPaymentParam
     * @return
     */
    ResultBean save(LoanBusinessPaymentParam loanBusinessPaymentParam);




    /**
     * 业务申请单详情页
     * @param orderId
     * @return
     */
    ResultBean detail(Long orderId);

    ResultBean appDetail(Long orderId);
}
