package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VideoFaceLogDO {
    private Long id;

    private Long orderId;

    private Long guaranteeCompanyId;

    private String guaranteeCompanyName;

    private Long customerId;

    private String customerName;

    private String customerIdCard;

    private String path;

    private Boolean type;

    private Long auditorId;

    private String auditorName;

    private Boolean action;

    private String latlon;

    private String location;

    private Long carDetailId;

    private String carName;

    private BigDecimal carPrice;

    private BigDecimal expectLoanAmount;

    private BigDecimal photoSimilarityDegree;

    private Date gmtCreate;

    private Date gmtModify;
}