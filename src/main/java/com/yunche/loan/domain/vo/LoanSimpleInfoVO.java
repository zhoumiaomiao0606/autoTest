package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/13
 */
@Data
public class LoanSimpleInfoVO {
    /**
     * 主贷人姓名
     */
    private String principalCustName;

    private String idCard;

    private String mobile;

    private String loanAmount;

    private String area;

    private String partner;

    private String bank;
    /**
     * （征信）申请单创建时间
     */
    private Date createTime;
}
