package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserGroupVO {

    private Long id;

    private String name;

    private String info;
    /**
     * 部门层级ID
     */
    private List<BaseVO> department;
    /**
     * 区域(城市)
     */
    private List<BaseVO> area;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;
}