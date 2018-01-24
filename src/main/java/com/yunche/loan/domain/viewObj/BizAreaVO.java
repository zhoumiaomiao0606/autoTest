package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
@Data
public class BizAreaVO {
    private Long id;

    private String name;
    /**
     * 父业务区域
     */
    private Parent parent;
    /**
     * 部门负责人
     */
    private Leader leader;
    /**
     * 业务区域等级
     * 根据父level自动+1计算
     */
    private Integer level;
    /**
     * 说明
     */
    private String info;

    private Date gmtCreate;

    private Date gmtModify;


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

    /**
     * 级联对象
     */
    @Data
    public static class Level extends LevelVO {
        private Integer level;
    }
}
