package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalRemitDetails {

    private String remit_beneficiary_bank;
    private String remit_beneficiary_account;
    private String remit_beneficiary_account_number;
    private String remit_amount;
    private String remit_return_rate_amount;

}
