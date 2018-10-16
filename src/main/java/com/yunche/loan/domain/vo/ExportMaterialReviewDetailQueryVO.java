/**
 * @author: ZhongMingxiao
 * @create: 2018-08-03 00:14
 * @description: 导出excel资料审核明细
 **/
package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ExportMaterialReviewDetailQueryVO
{
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

    //银行分期本金
    private String bank_period_principal;

    //垫款日期
    private String remitdate;

    //资料接收日期  ---合同资料合伙人至公司
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
