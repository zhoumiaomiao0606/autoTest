package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
@Data
public class CreditApplyOrderParam {
    /**
     * 业务单号
     */
    private Long orderId;
    /**
     * 关联的-贷款基本信息
     */
    private LoanBaseInfoParam loanBaseInfo;
    /**
     * 主贷人
     */
    private CustomerParam principalLender;
    /**
     * 共贷人列表
     */
    private List<CustomerParam> commonLenderList = Collections.EMPTY_LIST;
    /**
     * 担保人列表
     */
    private List<CustomerParam> guarantorList = Collections.EMPTY_LIST;
    /**
     * 紧急联系人列表
     */
    private List<CustomerParam> emergencyContactList = Collections.EMPTY_LIST;
}
