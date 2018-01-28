package com.yunche.loan.domain.QueryObj;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Data
public class EmployeeQuery extends BaseQuery {

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
     * 上级ID（直接上级ID）
     */
    private Long parentId;
    /**
     * 员工类型（1：正式员工; 2：外包员工）
     */
    private Byte type;
    /**
     * 职位
     */
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
