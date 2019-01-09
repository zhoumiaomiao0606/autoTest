package com.yunche.loan.domain.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ParternerRuleParam
{
    //合伙人id
    private Long partnerId;
    //收费是否月结(0:否;1:是)
    private String payMonth;
    //车辆类型：0-新车; 1-二手车; 2-不限;
    private String carType;
    //贷款金额
    private String financialLoanAmount;
    //银行分期本金
    private String financialBankPeriodPrincipal;
    //贷款利率
    private String rate;

    private String bankRate;
    //年限
    private String year;
    //GPS数量
    private String carGpsNum;
    //银行ID
    private String bankAreaId;
    //上牌地城市ID
    private String areaId;

    private String bail;

    //返利金额
    private String partnerRebateAmount;

    @Override
    public String toString() {
        return JSON.toJSONString(this, new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.UseISO8601DateFormat });

    }
}
