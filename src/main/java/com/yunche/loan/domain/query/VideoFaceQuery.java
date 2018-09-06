package com.yunche.loan.domain.query;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/6/20
 */
@Data
public class VideoFaceQuery extends BaseQuery {

    private Long id;

    private Long orderId;
    /**
     * 担保公司ID
     */
    private Long guaranteeCompanyId;
    /**
     * 担保公司名称
     */
    private String guaranteeCompanyName;

    private Long customerId;

    private String customerName;

    private String customerIdCard;

    private String path;

    private Byte type;

    private Long auditorId;

    private String auditorName;

    private Byte action;

    private String latlon;

    private String address;

    private Long carDetailId;

    private String carName;

    private BigDecimal carPrice;

    private BigDecimal expectLoanAmount;

    private BigDecimal photoSimilarityDegree;

    private Long bankId;

    private String bankName;

    private Date gmtCreate;

    private Date gmtModify;

    private String videoSize;
}
