package com.yunche.loan.domain.valueObj;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
@Data
public class BizAreaVO {
    private Long id;

    private String name;

    private Integer level;

    private String info;

    private Date gmtCreate;

    private Date gmtModify;

    private Long parentId;

    /**
     * 部门负责人
     */
    private Head head;
    /**
     * 当前区域覆盖城市列表
     * 省/市/区
     */
    private List<Area> areas = Lists.newArrayList();

    @Data
    public static class Head {
        private Long employeeId;
        private String employeeName;
    }

    @Data
    public static class Area {
        private Long provId;
        private Long cityId;
        private Long areaId;
        private String prvoName;
        private String cityName;
        private String areaName;
    }

}
