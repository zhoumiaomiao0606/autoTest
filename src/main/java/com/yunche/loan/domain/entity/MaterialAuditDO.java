package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MaterialAuditDO {
    private Long id;

    private Date complete_material_date;

    private Byte status;

    private String feature;

    private String remark;
}