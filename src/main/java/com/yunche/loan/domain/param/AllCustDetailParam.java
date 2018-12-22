package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/2/26
 */
@Data
public class AllCustDetailParam {
    /**
     * 主贷人
     */
    private CustomerParam principalLender;
    /**
     * 共贷人列表
     */
    private List<CustomerParam> commonLenderList;
    /**
     * 担保人列表
     */
    private List<CustomerParam> guarantorList;
    /**
     * 紧急联系人列表
     */
    private List<CustomerParam> emergencyContactList;

    /**
     * 特殊关联人列表
     */
    private List<CustomerParam> specialContactList;
}
