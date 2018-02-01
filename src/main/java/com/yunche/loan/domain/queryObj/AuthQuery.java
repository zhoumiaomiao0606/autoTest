package com.yunche.loan.domain.queryObj;

import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/2/2
 */
@Data
public class AuthQuery extends BaseQuery {
    /**
     * 用户组ID
     */
    private Long userGroupId;
    /**
     * 名称
     */
//    private String name;
    /**
     * 区域ID
     */
//    private Long areaId;

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
