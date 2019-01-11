package com.yunche.loan.domain.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ParternerRuleSharpTuningeParam
{
    private Long orderId;
    //返利类型 取自上一个接口传递的值
    private byte costType;
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
    //年限
    private String year;
    //GPS数量
    private String carGpsNum;
    //规则信息集合
    private List<RuleDetailPara> listRule;
    //计算出来的金额，上一步接口提供
    private String value;
    //内扣方式：0-不内扣，1-返利内扣，2-打款内扣
    private String type;
    //第一道返利
    private String rebateFirst;
    //公司返利
   /* private String rebateCompany;*/

    private String bail;

    //合伙人返利金额
    private String partnerRebateAmount;

    private String rebateTeant;

    @Override
    public String toString() {
        return JSON.toJSONString(this, new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.UseISO8601DateFormat });

    }
}
