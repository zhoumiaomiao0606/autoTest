package com.yunche.loan.domain.QueryObj;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Data
public class EmployeeQuery extends BaseQuery {

    private Long id;

    private String name;

    private String idCard;

    private String mobile;

    private String email;

    private String dingDing;

    private Long departmentId;

    private Long leaderId;

    private String title;
    /**
     * 入职开始时间
     */
    private Date entryDateStart;
    /**
     * 入职截止时间
     */
    private Date entryDateEnd;
}
