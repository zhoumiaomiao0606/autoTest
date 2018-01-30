package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Data
public class DepartmentVO {
    private Long id;

    private String name;
    /**
     * 上级部门
     */
    private List<Long> parent;
    /**
     * 部门负责人
     */
    private List<Long> leader;
    /**
     * 区域
     */
    private List<Long> area;
    /**
     * 本部门员工总数
     */
    private Integer employeeNum;

    private String tel;

    private String fax;

    private String address;

    private String openBank;

    private String receiveUnit;

    private String bankAccount;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

}
