package com.yunche.loan.domain.param;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Data
public class LoanRefundApplyParam {

    private String refund_id;

    @NotBlank
    private String order_id;
    @NotBlank
    private String refund_amount;
    @NotBlank
    private String refund_date;
    @NotBlank
    private String refund_reason;
    @NotBlank
    private String refund_pay_open_bank;
    @NotBlank
    private String refund_pay_account_name;
    @NotBlank
    private String refund_pay_account;

    private List<String> path;

    public String getPath() {
        if(CollectionUtils.isEmpty(path)){
            return JSON.toJSONString(new ArrayList<>());
        }
        return JSON.toJSON(path).toString();
    }

}
