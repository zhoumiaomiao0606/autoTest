package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;
@Data
public class JtxCommunicationDO {
    private String jtxId;

    private Long bridgeProcecssId;

    private Long orderId;

    private String name;

    private String idcard;

    private Date createDate;

    private Date updateDate;

    private Integer orderStatus;

    private String errorInfo;

    private String assetNumber;

}