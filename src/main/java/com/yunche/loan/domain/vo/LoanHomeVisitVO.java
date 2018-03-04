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
    private Long id;

    private Long visitSalesmanId;

    private Date visitDate;

    private String surveyReport;

    private String visitAddress;

    private List<CustomerVO.File> files = Collections.EMPTY_LIST;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;
}