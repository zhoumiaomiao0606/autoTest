package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;

@Data
public class EmployeeDO {
    private Long id;

    private String name;

    private String idCard;

    private String mobile;

    private String email;

    private String dingDing;
    /**
     * 部门ID
     */
    private Long departmentId;
    /**
     * 直接主管ID
     */
    private Long parentId;

    private String title;

    private Date entryDate;
    /**
     * 员工类型（1:正式员工; 2:外包员工）
     */
    private Byte type;
    /**
     * 员工等级
     */
    private Integer level;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}