package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 10:48
 * @description:
 **/
@Data
public class MaterialReviewChartVO
{
    //大区
    private String biz_area;
    //业务区域
    private String area_name;

    //业务团队
    private String partner_name;

    //客户姓名
    private String customer_name;

    //身份证号
    private String id_card;

    //贷款银行
    private String bank;

    //申请征信时间
    private String credit_apply_time;

    //银行分期本金
    private String bank_period_principal;

    //垫款日期
    private String remitdate;

    //资料接收日期
    private String express_receive_date;

    //资料齐全日期
    private String complete_material_date;

    //资料审核提交日期
    private String materialReviewSubmit;

    //资料审核状态
    private String material_review;

    //资料增补次数
    private String supplementCount;

    //资料增补内容
    private String supplementContent;

    //提车资料提交时间
    private String carMaterialCreateTime;

    //资料增补时间
    private String supplementTime;

    //合同上交银行日期
    private String express_sendbank_date;

    //垫款超期天数
    private String remitOverdueTime;

    //纸审超期天数
    private String revievOverdueTime;
}
