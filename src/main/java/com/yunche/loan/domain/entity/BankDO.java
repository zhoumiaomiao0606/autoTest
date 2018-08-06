package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BankDO {
    private Long id;

    private String name;

    private String mnemonicCode;

    private String contact;

    private Long carLicenseLocation;

    private String tel;

    private String officePhone;

    private String fax;

    private String address;

    private Byte needVideoFace;

    private Byte videoFaceMachine;


    //0 启用 1 停用  2 删除
    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;
}