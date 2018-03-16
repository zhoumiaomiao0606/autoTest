package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class InstallGpsDO {
    private Long id;

    private Long order_id;

    private String gps_number;

    private String feature;

    private Byte status;
}