package com.yunche.loan.domain.viewObj;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/2/26
 */
@Data
public class LoanBaseInfoVO {

    private Long id;

    private Long partnerId;

    private Long salesmanId;

    private Long areaId;

    private Byte carType;

    private String bank;

    private Byte loanAmount;
}
