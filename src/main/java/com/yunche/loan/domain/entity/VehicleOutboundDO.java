package com.yunche.loan.domain.entity;


import lombok.Data;

@Data
public class VehicleOutboundDO extends VehicleOutboundDOKey
{
    private String reason;

    private String address;

    private String specificAddress;

    private String customerCondition;

    private String progress;

    private String result;

    private String remarks;

    private String balance;
}
