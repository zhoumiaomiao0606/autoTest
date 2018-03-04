package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Date;

/**
 * 业务单详情信息
 *
 * @author liuzhe
 * @date 2018/2/27
 */
@Data
public class InstProcessOrderVO {
    /**
     * 业务单号
     */
    private String id;
    /**
     * 关联的-贷款基本信息
     */
    private LoanBaseInfoVO loanBaseInfo;
    /**
     * 关联的-客户信息(主贷人/共贷人/担保人/紧急联系人)
     */
    private CustDetailVO custDetail;
}
