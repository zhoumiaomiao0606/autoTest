package com.yunche.loan.obj;

import lombok.Data;

@Data
public class BaseAeraDO {
    private Integer id;

    private Long codeId;

    private Long parentCodeId;

    private String positionName;

    private Byte level;

}