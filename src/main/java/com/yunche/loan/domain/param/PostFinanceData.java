package com.yunche.loan.domain.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PostFinanceData
{
    private long partner;

    private Byte type;//1代客户垫款 2收到银行款项 3客户退款 4公司代客户偿款

    private BigDecimal clientDepositReturn;

    private BigDecimal clientAdvance;

    private BigDecimal companyCompensatory;

    private String companyId;//公司银行账户id


    @Override
    public String toString() {
        return JSON.toJSONString(this, new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.UseISO8601DateFormat });

       }
}
