package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.List;

/**
 * 权限-级联列表
 *
 * @author liuzhe
 * @date 2018/1/24
 */
@Data
public class AuthVO {

    private List<Menu> menus;

    @Data
    public static class Menu {
        private Long id;
        private String name;
        private String uri;
        private List<Menu> menus;
        private List<Page> pages;
    }

    @Data
    public static class Page {
        private Long id;
        private String name;
        private List<Operation> operations;
    }

    @Data
    public static class Operation {
        private Long id;
        private String name;
    }
}
