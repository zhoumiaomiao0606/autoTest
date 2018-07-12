package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/7/9
 */
@Data
public class UniversalDataFlowDetailVO {

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


    private Long id;

    private Long flowOutDeptId;

    private String flowOutDeptName;

    private Long flowInDeptId;

    private String flowInDeptName;

    private Byte expressCom;

    private String expressNum;

    private Date expressSendDate;

    private Date expressReceiveDate;

    private String expressReceiveMan;

    private Byte hasMortgageContract;

    private Byte type;

    private Byte status;

    private String info;

    private String taskKey;

    private Date gmtCreate;

    private Date gmtModify;
}
