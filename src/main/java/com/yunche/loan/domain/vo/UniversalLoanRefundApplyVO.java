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
    private String refund_pay_open_bank;
    private String refund_pay_account_name;
    private String refund_pay_account;
    private String path;

    public List<String> getPath() {
        if (StringUtils.isBlank(path)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(path, String.class);
    }
}
