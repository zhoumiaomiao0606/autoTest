package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MaterialAuditDO {
    private Long id;

    private Date complete_material_date;

    private Byte rate_type;

    private Byte is_pledge;

    private Byte is_guarantee;

    private Byte status;

    private String feature;
}