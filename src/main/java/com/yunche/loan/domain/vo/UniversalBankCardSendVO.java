package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
@Data
public class UniversalBankCardSendVO {

    /**
     * 业务单号
     */
    private String orderId;
    /**
     * 主贷人姓名
     */
    private String customerName;
    /**
     * 身份证号码
     */
    private String idCard;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 业务员
     */
    private String salesmanName;
    /**
     * 业务团队
     */
    private String partnerName;
    /**
     * 业务组织
     */
    private String departmentName;
    /**
     * 贷款银行
     */
    private String bankName;
    /**
     * 银行卡寄送地址
     */
    private String bankCardTransmitAddress;


    private String cardholderName;

    private String cardholderPhone;

    private String cardholderAddress;

    private String repayCardNum;

    private String expressSendAddress;

    private Byte expressCom;
    private String expressComText;

    private String expressSendNum;

    private Date expressSendDate;

    private Date gmtCreate;

    private Date gmtModify;

    public String getCardholderName() {
        if (null == cardholderName) {
            return customerName;
        }
        return cardholderName;
    }

    public String getCardholderPhone() {
        if (null == cardholderPhone) {
            return mobile;
        }
        return cardholderPhone;
    }

    public String getCardholderAddress() {
        if (null == cardholderAddress) {
            return bankCardTransmitAddress;
        }
        return cardholderAddress;
    }
}
