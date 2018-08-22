package com.yunche.loan.domain.entity;

import java.util.Date;

public class OrderHandleResultDO {
    private Long orderid;

    private Byte handletype;

    private String handdlePerson;

    private Date trailVehicleDate;

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public Byte getHandletype() {
        return handletype;
    }

    public void setHandletype(Byte handletype) {
        this.handletype = handletype;
    }

    public String getHanddlePerson() {
        return handdlePerson;
    }

    public void setHanddlePerson(String handdlePerson) {
        this.handdlePerson = handdlePerson == null ? null : handdlePerson.trim();
    }

    public Date getTrailVehicleDate() {
        return trailVehicleDate;
    }

    public void setTrailVehicleDate(Date trailVehicleDate) {
        this.trailVehicleDate = trailVehicleDate;
    }
}