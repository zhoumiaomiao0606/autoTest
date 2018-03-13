package com.yunche.loan.domain.entity;

import java.math.BigDecimal;

public class RemitDetailsDO {
    private Long id;

    private String beneficiary_bank;

    private String beneficiary_account;

    private String beneficiary_account_number;

    private BigDecimal remit_amount;

    private BigDecimal return_rete_amount;

    private Byte status;

    private String feature;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBeneficiary_bank() {
        return beneficiary_bank;
    }

    public void setBeneficiary_bank(String beneficiary_bank) {
        this.beneficiary_bank = beneficiary_bank == null ? null : beneficiary_bank.trim();
    }

    public String getBeneficiary_account() {
        return beneficiary_account;
    }

    public void setBeneficiary_account(String beneficiary_account) {
        this.beneficiary_account = beneficiary_account == null ? null : beneficiary_account.trim();
    }

    public String getBeneficiary_account_number() {
        return beneficiary_account_number;
    }

    public void setBeneficiary_account_number(String beneficiary_account_number) {
        this.beneficiary_account_number = beneficiary_account_number == null ? null : beneficiary_account_number.trim();
    }

    public BigDecimal getRemit_amount() {
        return remit_amount;
    }

    public void setRemit_amount(BigDecimal remit_amount) {
        this.remit_amount = remit_amount;
    }

    public BigDecimal getReturn_rete_amount() {
        return return_rete_amount;
    }

    public void setReturn_rete_amount(BigDecimal return_rete_amount) {
        this.return_rete_amount = return_rete_amount;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }
}