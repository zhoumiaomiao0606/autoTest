package com.yunche.loan.domain.param;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
@Data
public class LoanBaseInfoParam {

    private Long id;

    private Long partnerId;

    private Long salesmanId;

    private Long areaId;

    private Byte carType;

    private String bank;

    private Byte loanAmount;
}
