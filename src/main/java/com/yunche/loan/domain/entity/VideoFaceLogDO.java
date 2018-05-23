package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class VideoFaceLogDO {
    private Long id;

    private Long orderId;

    private String guaranteeCompany;

    private Long customerId;

    private String customerName;

    private String customerIdCard;

    private String path;

    private Byte type;

    private Long auditorId;

    private String auditorName;

    private Byte action;

    private Date gmtCreate;

    private Date gmtModify;
}