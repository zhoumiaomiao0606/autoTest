package com.yunche.loan.domain.viewObj;

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
    /**
     * 父业务区域 ID层级列表
     */
    private List<BaseVO> parent;
    /**
     * 部门负责人 ID层级列表
     */
    private List<BaseVO> leader;
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
}
