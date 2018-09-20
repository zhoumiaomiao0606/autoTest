package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 09:22
 * @description:
 **/
@Data
public class BankCreditChartVO
{
    //大区
    private String biz_area;

    //业务区域
    private String area_name;

    //业务关系
    private String cust_type;

    //客户姓名
    private String customer_name;

    //身份证号
    private String customer_id_card;

    //手机号
    private String customer_mobile;

    //贷款银行
    private String bank;

    //担保类型
   /* private String guaranteeType;*/

    //业务团队
    private String partner_name;

    //业务员
    private String salesman_name;

    //主贷人姓名
    private String principal_name;

    //与主贷人关系
    private String cust_relation;

    //征信结果
    private String credit_result;

    //征信申请时间
    private Date credit_apply_time;

    //征信提交时间
    private Date credit_query_time;

    //提交人
    private String gmt_user;
}
