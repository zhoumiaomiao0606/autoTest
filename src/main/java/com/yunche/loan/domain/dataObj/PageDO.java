package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PageDO {
    private Long id;

    private String name;

    private String uri;

    private Long menuId;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private Byte status;

    /**
     * 所属菜单
     * <p>
     * page所属的menu
     */
    private MenuDO menu;
    /**
     * 页面下所有操作
     * <p>
     * page下的Operation列表
     */
    private List<OperationDO> operations;
}