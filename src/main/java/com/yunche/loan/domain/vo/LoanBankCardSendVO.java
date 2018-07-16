package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
@Data
public class LoanBankCardSendVO {

    private String orderId;

    private String cardholderName;

    private String cardholderPhone;

    private String cardholderAddress;

    private String repayCardNum;

    private String expressSendAddress;

    private String expressSendNum;

    private Date expressSendDate;

    private Date gmtCreate;

    private Date gmtModify;
}
