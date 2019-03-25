package com.yunche.loan.domain.param;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LoanRefundApplyParam {

    private Long refund_id;





    private Long order_id;

    private String refund_amount;

    private String refund_date;

    private String refund_reason;

    private String refund_cause;

    private String retrun_reason;

    private String return_text;
    /**
     * 退款账号ID
     */

    private Long refund_apply_account_id;

    private List<String> path;


    private BigDecimal advances_interest;//垫款利息收入

    private BigDecimal other_interest;//其他利息收入

    private BigDecimal penalty_interest;//罚息收入

    public String getPath() {
        if (CollectionUtils.isEmpty(path)) {
            return "[]";
        }
        return JSON.toJSONString(path);
    }

}
