package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Date;

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
    private Parent parent;
    /**
     * 部门负责人
     */
    private Leader leader;
    /**
     * 区域
     */
    private Area area;
    /**
     * 部门人数
     */
    private Integer num;

    private String tel;

    private String fax;

    private String address;

    private String openBank;

    private String receiveUnit;

    private String bankAccount;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    @Data
    public static class Parent {
        private Long id;
        private String name;
    }

    @Data
    public static class Leader {
        private Long id;
        private String name;
    }

    @Data
    public static class Area {
        private Long id;
        private String name;
    }
}
