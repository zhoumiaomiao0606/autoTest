package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ConfVideoFaceTimeDO {

    private Long id;

    private Long bankId;

    private BigDecimal startLoanAmount;

    private BigDecimal endLoanAmount;

    private String startTime;

    private String endTime;

    /**
     * 人工面签-最大等待时间（单位：分钟；   -1表示无限等待）
     */
    private Integer maxWaitTime;

    /**
     * 类型：1-工作日;2-周末;3-节假日;
     */
    private Byte type;

    private Date gmtCreate;

    private Date gmtModify;
}