package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/11/12
 */
@Data
public class LoanCustRoleChangeHisDetailVO {

    /**
     * 变更前
     */
    private List<CustomerVO> before = Collections.EMPTY_LIST;

    /**
     * 变更后
     */
    private List<CustomerVO> after = Collections.EMPTY_LIST;
}
