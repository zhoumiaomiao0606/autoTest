package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalCustomerDetailVO {

    private String customer_id;//客户id
    private String cust_type;//客户类型: 1-主贷人;2-共贷人;3-担保人;4-紧急联系人;
    private String name;//姓名
    private String id_card;//身份证
    private String mobile;//手机号
    private String age;//年龄
    private String sex;//性别 1男 2女
    private String apply_date;//申请时间
    private String address;//现居地址
    private String marry;//是否结婚 0未婚 1已婚
    private String identity_address;//身份证地址
    private String mobile_area;//手机号归属地
    private String education;//学历：1-高中及以下;2-专科;3-本科;4-硕士;5-博士;
    private String company_name;//单位名字
    private String company_phone;//单位电话
    private String month_income;//月入
    private String house_type;//房产情况:1-自有商品房有贷款;2-自有商品房无贷款;
    private String house_owner;//房产所有人:1-本人所有;2-夫妻共有;
    private String house_feature;//房产性质：1-商品房有贷款;2-商品房无贷款;
    private String house_address;//房产地址
    private String bank_gmt_create;//银行征信查询日期
    private String bank_addition;//银行征信附加条件
    private String bank_result;//银行征信结果
    private String bank_result_info;//银行征信内容
    private String society_gmt_create;//社会征信查询日期
    private String society_addition;//社会征信附加条件
    private String society_result;//社会征信结果
    private String society_result_info;//社会征信内容
}
