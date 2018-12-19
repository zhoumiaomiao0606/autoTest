package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalRemitDetails {

    private String remit_beneficiary_bank;

    private String remit_bank_code;
    private String remit_beneficiary_account;
    private String remit_beneficiary_account_number;

    private String remit_bank;


    private String remit_account;
    private String remit_account_number;

    private String remit_amount;
    private String remit_return_rate_amount;

    private String remit_insurance_situation;//保险情况


    private String remit_is_sendback;

    private String remit_payment_organization;
    private String remit_application_date;
    private String remark;

    private Byte remit_status;

}
