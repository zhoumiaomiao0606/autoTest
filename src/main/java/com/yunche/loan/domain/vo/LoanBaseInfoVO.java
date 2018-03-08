package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/2/26
 */
@Data
public class LoanBaseInfoVO {

    private Long id;

    private BaseVO partner;

    private BaseVO salesman;

    private BaseVO area;

    private Byte carType;

    private String bank;

    private Byte loanAmount;
}
