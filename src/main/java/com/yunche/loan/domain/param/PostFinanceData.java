package com.yunche.loan.domain.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PostFinanceData
{
    private long partnerId;//合伙人ID

    private Long bankId;//银行编号

    private Byte type;//1代客户垫款 2收到银行款项 3客户退款 4公司代客户偿款

    private BigDecimal clientDepositReturn;//客户退款

    private BigDecimal clientAdvance;//代客户垫款

    private BigDecimal companyCompensatory;//公司代客户偿款

    private String companyId;//公司银行账户id


    //--001
    //private String amountMoney ;//打款金额

    //--003
    private String amountMoney ;//打款金额
    private String advancesInterest ;//垫款利息收入
    private String otherInterest ;//其他利息收入
    private String penaltyInterest ;//罚息收入




    //--008
    private String companySubrogationFund ;//应收公司代位追偿款
    private String partnerSubrogationFund ;//应收合伙人代位追偿款
    private String subrogationFundInterest ;//应收代位追偿款利息
    private String subrogationFundIncome ;//应收代位追偿款费用

    @Override
    public String toString() {
        return JSON.toJSONString(this, new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.UseISO8601DateFormat });

       }
}
