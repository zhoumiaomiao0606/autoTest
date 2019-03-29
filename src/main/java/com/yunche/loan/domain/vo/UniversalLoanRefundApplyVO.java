package com.yunche.loan.domain.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class UniversalLoanRefundApplyVO {

    private String refund_id;
    private String order_id;
    private String refund_amount;
    private String refund_date;
    private String refund_reason;
    private String refund_cause;
    private String retrun_reason;
    private String return_text;
    private String refund_apply_account_id;
    private String path;

    private String advances_interest;
    private String other_interest;
    private String penalty_interest;
    private String refund_pay_open_bank;
    private String refund_pay_account_name;
    private String refund_pay_account;

    public List<String> getPath() {
        if (StringUtils.isBlank(path)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(path, String.class);
    }
}
