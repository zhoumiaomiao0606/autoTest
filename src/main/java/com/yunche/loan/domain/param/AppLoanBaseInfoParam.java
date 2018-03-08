package com.yunche.loan.domain.param;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
@Data
public class AppLoanBaseInfoParam {
    /**
     * 业务单ID
     */
    private Long orderId;
    /**
     * 贷款基本信息
     */
    private LoanBaseInfoParam loanBaseInfo;
}
