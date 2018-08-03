/**
 * @author: ZhongMingxiao
 * @create: 2018-08-03 09:19
 * @description: 抵押超期
 **/
package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ExportMortgageOverdueQueryVO
{
    //业务区域
    private String area_name;

    //业务团队
    private String partner_name;

    //客户姓名
    private String customer_name;

    //身份证号
    private String id_card;

    //手机号
    private String customer_mobile;

    //贷款银行
    private String bank;

    //车辆型号
    private String car_type;

    //车牌号
    private String license_plate_number;

    //车价
    private String price;

    //贷款金融
    private String loan_amount;

    //银行分期本金
    private String bank_period_principal;

    //垫款日期
    private String remitdate;

    //银行放款日期
    private String lend_date;

    //抵押资料公司寄合伙人
    private String sendMaterialToParter_date;

    //抵押资料合伙人接收时间
    private String ParterReceiveMaterial_date;

    //抵押状态
    private String apply_license_plate_deposit_info;

    //抵押日期
    private String depositTime;

    //抵押超期天数
    private String depositOverdueTime;

    //提交人
    private String submitUser;
}
