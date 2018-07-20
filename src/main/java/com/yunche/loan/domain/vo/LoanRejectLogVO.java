package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/4/14
 */
@Data
public class LoanRejectLogVO {

    private Long id;

    private String orderId;

    private String rejectOriginTask;

    private String rejectToTask;

    private Date gmtCreate;

    private String reason;
}
