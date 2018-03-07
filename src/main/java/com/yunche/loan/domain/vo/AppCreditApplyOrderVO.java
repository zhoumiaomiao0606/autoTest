package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Data
public class AppCreditApplyOrderVO {
    /**
     * 业务单号
     */
    private String id;
    /**
     * 关联的-贷款基本信息
     */
    private LoanBaseInfoVO loanBaseInfo;
    /**
     * 主贷人
     */
    private CustomerVO principalLender;
    /**
     * 共贷人列表
     */
    private List<CustomerVO> commonLenderList = Collections.EMPTY_LIST;
    /**
     * 担保人列表
     */
    private List<CustomerVO> guarantorList = Collections.EMPTY_LIST;
    /**
     * 紧急联系人列表
     */
    private List<CustomerVO> emergencyContactList = Collections.EMPTY_LIST;
}
