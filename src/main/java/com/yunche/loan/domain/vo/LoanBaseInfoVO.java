package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/2/26
 */
@Data
public class LoanBaseInfoVO {

    private Long id;
    /**
     * 合伙人对象
     */
    private BaseVO partner;
    /**
     * 业务员对象
     */
    private BaseVO salesman;
    /**
     * 区域对象
     */
    private BaseVO area;

    private Byte carType;

    private String bank;

    private Byte loanAmount;
}
