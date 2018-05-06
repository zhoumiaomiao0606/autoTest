package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MaterialDownHisDO extends MaterialDownHisDOKey {
    private String url;

    private Byte status;

    private Date gmtCreate;


}