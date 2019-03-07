package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

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

    /**
     * 城市id---用于判断是否城市是台州
     */
    private Long cityId;

    /**
     * WEB端回填：级联区域ID列表
     */
    private List<Long> cascadeAreaId;

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

    /**
     * 业务组织
     */
    private  String departmentName;
}
