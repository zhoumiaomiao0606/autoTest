package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanRefundApplyDO {

    private Long id;

    private Long order_id;

    private Long initiator_id;

    private String initiator_name;

    private Long auditor_id;

    private String auditor_name;

    private BigDecimal refund_amount;

    private Date refund_date;
    /**
     * 退款原因(1-金融方案修改;2-退款不做;3-业务审批重审;)
     */
    private Byte refund_reason;

    private Byte refund_cause;
    /**
     * 退款账号详情ID
     */
    private Long refund_apply_account_id;

    private Date start_time;

    private Date end_time;

    private Byte status;

    private String path;

    private BigDecimal advances_interest;

    private BigDecimal other_interest;

    private BigDecimal penalty_interest;

    private String return_text;

    private Byte retrun_reason;
}