package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.List;

/**
 * 征信申请单
 *
 * @author liuzhe
 * @date 2018/2/28
 */
@Data
public class CreditApplyVO {
    /**
     * 贷款基本信息
     */
    private LoanBaseInfoVO loanBaseInfo;
    /**
     * 主贷人/共贷人列表/担保人列表/紧急联系人列表
     */
    private CustDetailVO custDetail;
}
