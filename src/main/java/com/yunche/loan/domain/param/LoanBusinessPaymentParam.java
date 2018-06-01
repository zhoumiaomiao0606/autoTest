package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.Date;

@Data
public class LoanBusinessPaymentParam {

    private Long orderId;

    private Date remit_application_date;

    private String remit_beneficiary_bank;

    private String remit_beneficiary_account;

    private String remit_beneficiary_account_number;


    private Byte remit_is_sendback;

    private String remark;

}
