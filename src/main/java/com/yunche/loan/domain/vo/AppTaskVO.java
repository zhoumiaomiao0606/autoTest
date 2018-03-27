package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/27
 */
@Data
public class AppTaskVO {

    private String id;

    private String customer;
    private String salesman;
    private String partner;

    private String idCard;
    private String mobile;

    private String orderGmtCreate;

    private String overdueNum;
    private String taskStatus;

    private String currentTask;

}
