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

    private String refund_pay_open_bank;

    private String refund_pay_account_name;

    private String refund_pay_account;

    private Date start_time;

    private Date end_time;

    private Byte status;

    private String path;
}