package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author jjq
 * @date 2018/7/16
 */
@Data
public class GpsDetailVO {
    /**
     * 业务单号
     */
    private String orderId;
    /**
     * 主贷人姓名
     */
    private String customerName;
    /**
     * 身份证号码
     */
    private String idCard;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 业务员
     */
    private String salesmanName;
    /**
     * 业务团队
     */
    private String partnerName;
    /**
     * 业务组织
     */
    private String departmentName;
    /**
     * 贷款银行
     */
    private String bankName;

    private String carType;

    private String carDetailName;

    private String licensePlateNymber;

    private int gpsNum;

    private String gpsCompany;

}
