package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.Date;

@Data
public class LitigationParam {
    private Long id;

    private Long orderId;

    private Date registerDate;

    private String registerId;

    private String plaintiff;

    private String defendant;

    private String ruleCourt;

    private String litigationTotal;

    private String litigationMoney;

    private String undertakeCourt;

    private String undertakeCourtTel;

    private String clerk;

    private String clerkTel;

    private Date sittingDate;

    private String publicationFee;

    private String sentence;

    private Date preservationDate;

    private String preservationMoney;

    private String preservationFee;

    private String preservationJudge;

    private Date effectDate;

    private String preservationJudgeTel;

    private Date returnDate;

    private String returnMoney;

    private String remarks;

    private String litigationNo;
}