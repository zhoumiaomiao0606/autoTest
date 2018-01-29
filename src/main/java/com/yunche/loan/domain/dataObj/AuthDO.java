package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;

@Data
public class AuthDO {
    private Long id;

    private Long sourceId;

    private Byte type;

    private Date gmtCreate;

    private Date gmtModify;
}