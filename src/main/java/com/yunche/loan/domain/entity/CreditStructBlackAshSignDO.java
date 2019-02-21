package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CreditStructBlackAshSignDO {

    private Long id;

    private Byte hasForbidSign;

    private Byte hasFollowSign;

    private Byte hasRefuseSign;

    private Byte hasBlackListSign;

    private Byte isAshList;

    private Byte isOnceAshList;

    private Byte isProcessAshList;

    private Byte isBlackList;

    private Byte isExtCheat;

    private Date gmtCreate;

    private Date gmtModify;
}