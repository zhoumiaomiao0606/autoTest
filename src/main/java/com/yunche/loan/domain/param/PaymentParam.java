package com.yunche.loan.domain.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class PaymentParam
{

    //非必填
    private String cnaps;//联行号

    private String bank_name;//收款银行全称

    private String branch_bank_name;//收款银行支行名称

    //必填
    private String merchan_no = "60099001";//商户编号 传“60099001”

    private String bank_code;

    private Long order_id;//订单号

    private BigDecimal amount;//打款金额

    private String account_name;//收款帐户的开户名称

    private String account_number;//收款帐户的帐户号


    //非必填
    private String account_type;//账户类型

    private String province;//收款行省份编码

    private String city;//收款行城市编码


    //必填
    private List<Map<String,String>> debitInfo;//垫资人信息

    private String debit_name;//购车人姓名

    private String debit_cert_no;//购车人身份证号

    private String debit_mobile_no;//购车人手机号码

    private String urgency = "1";//是否需要实时出款,只能填写0或者1 。“ 1”表示实时出款，“0”表示非实时出款

    //非必填
    private String debit_address;//购车人地址


    private String leave_word;//留言

    private String abstract_info;//摘要

    private String call_back_url;//终态回调地址

    private String serial_no;//终态回调地址

    private Integer source_type = 1;






    @Override
    public String toString() {
        return JSON.toJSONString(this, new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.UseISO8601DateFormat });

    }
}
