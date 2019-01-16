package com.yunche.loan.domain.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BankCodeDO {
    private Integer id;

    private String code;

    private String name;

    private Byte level;

    private Integer parentId;

    private Byte status;

}