package com.yunche.loan.domain.query;

import lombok.Data;

import java.util.List;

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
     * 名称
     */
    private String name;
    /**
     * 区域ID
     */
    private Long areaId;
    /**
     * 区域ID列表
     */
    private List<Long> areaIdList;

    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 菜单ID列表
     */
    private List<Long> menuIdList;
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
}
