package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BankCardRecordVO {

    /**
     * 业务单号
     */
    private String  orderId;
    /**
     *主贷人姓名
     */
    private String principalLenderName;
    /**
     *身份证号码
     */
    private String idCard;
    /**
     *业务员
     */
    private String salesman;
    /**
     *业务团队
     */
    private String  partnerName;
    /**
     *业务组织
     */
    private String departmentName;
    /**
     *贷款银行
     */
    private String  bank;
    /**
     * 银行放款日
     */
    private Date lendDate;
    /**
     * 账单日
     */
    private Date billingDate;
    /**
     * 首月账单日
     */
    private Date firstBillingDate;

    /**
     * 还款日
     */
    private Date  repayDate;

    /**
     * 首月还款日
     */

    private Date firstRepaymentDate;

    /**
     * 还款卡号
     */
    private  String repayCardId;
    /**
     * 接收日期
     */
    private Date receiveDate;
    /**
     * 接收人
     */
    private String sendee;
}
