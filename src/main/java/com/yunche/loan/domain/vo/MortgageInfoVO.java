package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-15 11:27
 * @description:
 **/
@Data
public class MortgageInfoVO
{
    //车牌号
    private String license_plate_number;

    // 车辆类别 -- 1.新车  2.二手车  3.不限
    private String car_type;

    //抵押权人
    private String bank;

    //上牌抵押日期
    private String apply_license_plate_deposit_date;

    //登记证书号
    private String registration_certificate_number;

    //财务垫款时间
    //垫款日期
    private String remitdate;

    //银行放款时间
    private String lend_date;



}
