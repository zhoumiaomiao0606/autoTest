package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
@Data
public class AppCreditApplyVO {
    /**
     * 业务单ID
     */
    private Long orderId;
    /**
     * 主贷人ID
     */
    private Long customerId;
}
