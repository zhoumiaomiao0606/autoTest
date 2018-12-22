package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class BankCreditPrincipalVO {

    private Long orderId;

    private String bank;
    private String createTime;
    private String cName;
    private String idCard;
    private String eName;
    private String pName;
    private String businessVariety;
	private String submitOrganization;
	private String bnakResult;
	private String creditApplyTime;
	private String approvalTime;
	private String result;
}
