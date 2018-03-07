package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.CustomerVO;
import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Data
public class AppLoanHomeVisitParam {
    /**
     * 业务单ID
     */
    private Long orderId;

    private Long id;

    private Long visitSalesmanId;

    private Date visitDate;

    private String surveyReport;

    private String visitAddress;

    private List<FileVO> files = Collections.EMPTY_LIST;

    private Byte status;
}
