package com.yunche.loan.domain.param;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/2/28
 */
@Data
public class ApprovalParam {
    /**
     * 业务单ID
     */
    private Long orderId;

    /**
     * 审核结果：0-REJECT / 1-PASS / 2-CANCEL / 3-资料增补
     */
    private Integer action;

    /**
     * 审核备注信息
     */
    private String info;
}
