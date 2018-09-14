package com.yunche.loan.domain.param;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LoanRefundApplyParam {

    private Long refund_id;

    @NotNull
    private Long order_id;
    @NotEmpty
    private String refund_amount;
    @NotEmpty
    private String refund_date;
    @NotEmpty
    private String refund_reason;
    /**
     * 退款账号ID
     */
    @NotNull
    private Long refund_apply_account_id;

    private List<String> path;

    public String getPath() {
        if (CollectionUtils.isEmpty(path)) {
            return "[]";
        }
        return JSON.toJSONString(path);
    }

}
