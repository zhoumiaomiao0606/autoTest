package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ScheduleTaskVO {
    private String taskDisStatus;
    private String receiveManId;
    private String receiveManName;
    //分配的唯一id
    private String taskId;

    //任务id
    private String processId;

    //任务key
    private String taskKey;

    //生成待办任务日期
    private String createScheduleDate;

    //任务描述
    private String taskDescription;

    //订单id
    private String orderId;

    //合伙人
    private String partner;

    //业务员
    private String salesman;

    //贷款银行
    private String bank;

    //客户姓名
    private String name;

    //身份证
    private String idCard;

    //手机号
    private String mobile;

    //车辆类型：1-新车; 2-二手车; 3-不限;
    private String carType;

    //车名
    private String carName;

    //首付比例
    private String downPaymentRatio;

    //贷款期数
    private String loanTime;

    //贷款金额
    private String loanAmount;

    //任务状态
    private String taskStatusExplanation;

    //征信状态
    private String usertaskCreditApplyVerifyStatus;

    //电审状态
    private String usertaskTelephoneVerifyStatus;

    //家访状态
    private String usertaskVisitVerifyStatus;

    //资料增补/合同
    private String usertaskInfoSupplementStatus;

    private String partnerId;

    private String salesmanId;

    private String supplementOrderId;

    private String supplementType;

    private String supplementTypeText;

    private String his_id;

    private String refund_id;

}
