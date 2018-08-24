package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OrderHandleResultDO {

    private Long orderid;

    private Byte handletype;

    private String handdlePerson;

    private Date trailVehicleDate;
}