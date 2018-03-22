package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/4
 */
@Data
public class LoanHomeVisitVO {
    /**
     * 上门家访单ID
     */
    private Long id;
    /**
     * 客户ID
     */
    private Long customerId;
    /**
     * 上门调查员ID
     */
    private Long visitSalesmanId;

    private Date visitDate;

    private String surveyReport;

    private String visitAddress;

    private List<FileVO> files = Collections.EMPTY_LIST;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;
}