package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FSysCompensationVO {

    private BigDecimal compensatoryAmount;//代偿应收金额
    private BigDecimal compensatoryPaid;//代偿实收金额
    private BigDecimal compensatoryRest;//代收金额

//    private List<FSysCompensationDetail> compensationDetails;


    private String customerName;
    private String customerCardId;
    private String customerPhone;
    private BigDecimal financialBankPeriodPrincipal;
    private String compensatoryTime;
    private String compensatoryMoney;
    private String proportion;
    private String partnerMoney;

//    public static class FSysCompensationDetail{
//        private String customerName;
//        private String customerCardId;
//        private String customerPhone;
//        private BigDecimal financialBankPeriodPrincipal;
//        private String compensatoryTime;
//        private String compensatoryMoney;
//        private String proportion;
//        private String partnerMoney;
//    }

}
