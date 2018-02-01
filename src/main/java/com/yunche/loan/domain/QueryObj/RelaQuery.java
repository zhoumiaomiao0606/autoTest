package com.yunche.loan.domain.QueryObj;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/26
 */
@Data
public class RelaQuery extends BaseQuery {
    /**
     * 员工ID
     */
    private Long employeeId;
    /**
     * 用户组ID
     */
    private Long userGroupId;
    /**
     * 部门ID
     */
    private Long departmentId;
    /**
     * 区域ID
     */
    private Long areaId;
    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 页面名称
     */
    private String pageName;
    /**
     * 操作名称
     */
    private String operationName;
    /**
     * 类型（员工类型...）
     */
    private Byte type;
    /**
     * 绑定状态
     */
    private Integer bindStatus;
}
