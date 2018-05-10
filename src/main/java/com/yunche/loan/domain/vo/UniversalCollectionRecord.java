package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalCollectionRecord {
    private String collection_row;
    private String collection_id;
    private String collection_order_id;
    private String collection_date;
    private String collection_man_id;
    private String collection_man_name;
    private String collection_is_repayment;
    private String collection_repayment_date;
    private String collection_permit_repayment_date;
    private String collection_cause;
    private String collection_remark;
}
