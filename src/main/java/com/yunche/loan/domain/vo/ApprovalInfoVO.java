package com.yunche.loan.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 审核信息对象
 *
 * @author liuzhe
 * @date 2018/3/23
 */
@Data
public class ApprovalInfoVO implements Serializable {

    private static final long serialVersionUID = -7256824959782835363L;

    /**
     * 审核人ID
     */
    private Long id;
    /**
     * 审核人名称
     */
    private String name;
    /**
     * 审核结果
     */
    private Integer action;
    /**
     * 审核备注
     */
    private String info;

    public ApprovalInfoVO(Long id, String name, Integer action, String info) {
        this.id = id;
        this.name = name;
        this.action = action;
        this.info = info;
    }
}
