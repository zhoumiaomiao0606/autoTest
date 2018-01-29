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
     * 菜单ID
     */
    private Long menuId;
    /**
     * 页面ID
     */
    private Long pageId;
    /**
     * 操作ID
     */
    private Long operationId;
}
