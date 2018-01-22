package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/20
 */
@Data
public class BizAreaParam {

    private Long id;

    private String name;

    private String info;
    /**
     * 绑定的父级ID
     */
    private Long parentId;
    /**
     * 绑定的负责人ID
     */
    private Long employeeId;
    /**
     * 绑定的城市列表
     */
    private List<Long> areaIdList;
    /**
     * 等级
     */
    private Integer level;

    private Date gmtCreate;

    private Date gmtModify;

}
