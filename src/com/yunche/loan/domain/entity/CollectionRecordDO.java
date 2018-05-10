package com.yunche.loan.domain.entity;

import java.util.Date;

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

    private Date gmt_create;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Date getCollection_date() {
        return collection_date;
    }

    public void setCollection_date(Date collection_date) {
        this.collection_date = collection_date;
    }

    public Long getCollection_man_id() {
        return collection_man_id;
    }

    public void setCollection_man_id(Long collection_man_id) {
        this.collection_man_id = collection_man_id;
    }

    public Byte getIs_repayment() {
        return is_repayment;
    }

    public void setIs_repayment(Byte is_repayment) {
        this.is_repayment = is_repayment;
    }

    public Date getRepayment_date() {
        return repayment_date;
    }

    public void setRepayment_date(Date repayment_date) {
        this.repayment_date = repayment_date;
    }

    public Date getPermit_repayment_date() {
        return permit_repayment_date;
    }

    public void setPermit_repayment_date(Date permit_repayment_date) {
        this.permit_repayment_date = permit_repayment_date;
    }

    public Byte getCause() {
        return cause;
    }

    public void setCause(Byte cause) {
        this.cause = cause;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(Date gmt_create) {
        this.gmt_create = gmt_create;
    }
}