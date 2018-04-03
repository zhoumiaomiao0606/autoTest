package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BankLendRecordVO {
    /**
     *业务编号
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
     * 垫资日期
     */
    private Date remitGmtCreate;
    /**
     * 放款日期
     */
    private Date  lendDate;
    /**
     * 放款金额
     */
    private BigDecimal lendAmount;

}
