package com.yunche.loan.domain.entity;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class CollectionRecordDO {
    private Long id;

    private Long order_id;

    private Date collection_date;

    private Long collection_man_id;

    private Byte is_repayment;

    private Date repayment_date;

    private Date permit_repayment_date;

    private Byte cause;

    private String remark;

    private Timestamp gmt_create;
}