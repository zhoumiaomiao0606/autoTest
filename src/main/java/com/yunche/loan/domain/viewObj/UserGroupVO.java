package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Date;

@Data
public class UserGroupVO {

    private Long id;

    private String name;

    private String info;
    /**
     * 部门
     */
    private BaseVO department;
    /**
     * 区域(城市)
     */
    private BaseVO area;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;
}