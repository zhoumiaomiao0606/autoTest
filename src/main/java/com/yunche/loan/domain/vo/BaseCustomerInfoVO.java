package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 16:53
 * @description:客户主要信息---结算、车辆出库、车辆处理、上门拖车Vo类
 **/
@Data
public class BaseCustomerInfoVO
{
    //业务编号
    private String order_id;

    private String material_num;

    //主贷人姓名
    private String customer_name;
    //联系电话
    private String customer_mobile;
    //身份证号码
    private String customer_id_card;
    //现住地址
    private String customer_address;
    //省、市、区
    private String customer_hprovince;

    private String customer_hcity;

    private String customer_hcounty;
    //身份证地址
    private String customer_identity_address;
    //单位名称
    private String customer_company_name;
    //单位地址
    private String customer_company_address;
    //单位电话
    private String customer_company_phone;
    //备用电话
    private String customer_reserve_mobile;
    //业务员
    private String salesman_name;
    //业务团队
    private String partner_name;
    //业务区域
    private String partner_biz_area;
    //业务来源
    private String car_business_source;

    //逾期金额
    private BigDecimal currArrears;

    //贷款金额
    private BigDecimal loanBanlance;

    //财务代偿金额
    private BigDecimal compensationAmount;

    //清收成本
    private BigDecimal finalCosts;

    //贷款银行
    private String bank;
    //车型

    //车牌号
    //车牌号
    private String license_plate_number;

    //车型  例如 奥迪
    private String car_name;
}
