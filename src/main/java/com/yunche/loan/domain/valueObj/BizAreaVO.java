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

    private Long parentId;

    private Integer level;

    private String info;

    private Date gmtCreate;

    private Date gmtModify;
    /**
     * 部门负责人
     */
    private Head head;

    @Data
    public static class Head {
        private Long employeeId;
        private String employeeName;
    }

}
