package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Date;

@Data
public class UserGroupVO {

    private Long id;

    private String name;

    private String info;
    /**
     * 部门
     */
    private Department department;
    /**
     * 城市
     */
    private Area area;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    @Data
    public static class Department {
        private Long id;
        private String name;
    }

    @Data
    public static class Area {
        private Long id;
        private String name;
    }

    /**
     * 用户组关联的用户(员工)列表
     */
    @Data
    public static class RelaEmployeeVO {
        private Long id;
        private String name;
    }
}