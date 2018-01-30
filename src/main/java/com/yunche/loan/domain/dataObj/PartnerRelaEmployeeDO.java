package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;

@Data
public class PartnerRelaEmployeeDO extends PartnerRelaEmployeeDOKey {
    private Date gmtCreate;

    private Date gmtModify;
}