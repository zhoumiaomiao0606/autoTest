package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class LoanCustRoleChangeHisDetailDO {
    private Long id;

    private Long roleChangeHisId;

    private Byte type;

    private Long customerId;

    private Long principalCustId;

    private Byte custType;

    private Byte custRelation;

    private Byte guaranteeType;

    private String guaranteeRela;
}