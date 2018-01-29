package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/29
 */
@Data
public class PageVO {

    private Long id;

    private String name;

    private String uri;

//    private Long menuId;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private Byte status;

    /**
     * 菜单层级关系
     * <p>
     * 从最子菜单往上递推到最顶级菜单
     */
    private Menu menu;

    @Data
    public static class Menu {

        private Long id;

        private String name;

        private Long parentId;

        private String uri;

        private Integer level;

        private Menu childMenu;
    }

    /**
     * 操作列表
     */
    private List<Operation> operations;

    @Data
    public static class Operation {
        private Long id;

        private String name;

        private String uri;
    }
}
