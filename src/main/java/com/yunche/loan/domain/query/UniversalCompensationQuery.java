package com.yunche.loan.domain.query;

import lombok.Data;

import java.util.Date;

@Data
public class UniversalCompensationQuery {

    private Long orderId;//业务单号

    private Date applyCompensationDate;//申请代偿日期

}
