package com.yunche.loan.domain.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class LegworkReimbursementUpdateParam {
    @NotNull
    private Long id;

    private String transFee;

    private String hotelFee;

    private String eatFee;

    private String busiFee;

    private String otherFee;

    private List<String> files;

    private String reimbursementAmount;

    private String bank;

    private String remitAccount;

    private String collectionBank;

    private String collectionAccount;

    private String collectionAccountNumber;

    private Date busiTime;
}
