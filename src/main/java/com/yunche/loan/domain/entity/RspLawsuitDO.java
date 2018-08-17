package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class RspLawsuitDO {
    private Long id;

    private String sortTimeString;

    private String dataType;

    private String body;

    private String title;

}