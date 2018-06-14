package com.yunche.loan.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/27
 */
@Data
public class AppTaskVO {

    private String id;

    private String salesmanId;
    private String customer;
    private String partnerId;
    private String partner;
    private String salesman;

    private String idCard;
    private String mobile;

    private String orderGmtCreate;

    private String overdueNum;
    private String taskStatus;
    private String bank;

    private String currentTask;

    /**
     * 任务类型：1-未提交;  2-打回;
     */
    private String taskType;
    /**
     * 任务类型文本：1-未提交;  2-打回;
     */
    private String taskTypeText;

    /**
     * 是否可以发起【征信增补】
     */
    private Boolean canCreditSupplement;
}
