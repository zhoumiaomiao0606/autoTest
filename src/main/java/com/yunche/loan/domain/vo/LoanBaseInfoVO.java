package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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
    /**
     * 预计贷款额 枚举 1：<13W   2：13~20W   3：>=20W
     */
    private Byte loanAmount;

    /**
     * 实际贷款金额
     */
    private String actualLoanAmount;

    /**
     * 贷款申请日期
     */
    private Date applyDate;
}