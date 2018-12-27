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



    //--002
    private BigDecimal bankDeposits;//银行存款  1

    private BigDecimal partnerRebates;//应付合伙人返利  1

    private BigDecimal mortgageDeposit;//抵押押金  1

    private BigDecimal riskFee;//风险金  1

    private BigDecimal customerDeposit;//客户保证金  1

    private BigDecimal carLoanMoney;//车贷业务代垫款 1


    private String otherDeposit;//其他押金

    private String rebateInternalSettlement;//返利内部结算

    private String commissionIncome;//手续费及佣金收入

    private String reviewIncome;//评审费收入

    private String homeIncome;//上门家访收入

    private String overIncome;//超年限收入

    private String agencyIncome;//代办费收入

    private String renewalIncome;//续保收入


    /*传入一个公司收益参数--财务自己计算*/
    private String companyIncome;//新加

    private String carServiceIncome;//汽车服务费收入

    private String servicePay;//应付技术服务费

    private String payablePremium;//应付担保费

    private String guaranteeserviceIncome;//应收担保服务费

    private String premiumsIncome;//担保费收入

    private String technologyServiceIncome;//应收科技服务费

    private String skillIncome;//技术服务收入



    private BigDecimal assessmentIncome;//公证评估费收入 1

    private BigDecimal otherIncome;//其他收入  1

    private BigDecimal cardIncome;//上省外牌收入 1

    private BigDecimal gpsIncome;//GPS使用费收入 1

   // private String advancesInterest;//垫款利息收入

  //  private String otherInterest;//其他利息收入

   // private String penaltyInterest;//罚息收入


    @Override
    public String toString() {
        return JSON.toJSONString(this, new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.UseISO8601DateFormat });

       }
}
