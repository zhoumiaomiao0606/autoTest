package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MaterialAuditDO {

    private Long id;

    private Date complete_material_date;

    private String remark;
    /**
     * 合同编号                         -资料流转共享字段
     */
    private String contractNum;
    /**
     * 是否含抵押资料 (0-否;1-是;)       -资料流转共享字段
     */
    private Byte hasMortgageContract;
}