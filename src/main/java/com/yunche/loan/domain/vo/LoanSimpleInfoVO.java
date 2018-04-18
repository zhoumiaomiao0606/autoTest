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
     * 主贷人ID
     */
    private Long customerId;
    /**
     * 主贷人姓名
     */
    private String customerName;

    private String idCard;

    private String mobile;

    private String loanAmount;

    private String area;

    private Long partnerId;

    private String partnerName;

    private String bank;
    /**
     * （征信）申请单创建时间
     */
    private Date createTime;

    /**
     * 业务员
     */
    private String salesMan;
}
