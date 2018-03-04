package com.yunche.loan.domain.query;

import lombok.Data;

import java.util.Date;

/**
 * Created by zhouguoliang on 2018/2/8.
 */
@Data
public class OrderListQuery extends BaseQuery {

    private String orderNbr;

    private String custName;

    private String phone;

    private String identityNumber;

    private Long areaId;

    private String prov;

    private String city;

    private Long partnerId;

    private String startDateString;

    private String endDateString;

    private Date startDate;

    private Date endDate;

    // 未提交节点
    private String unsubmitProcessTask;
    // 未审核节点
    private String todoProcessTask;
    // 已审核节点
    private String doneProcessTask;
}
