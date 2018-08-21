package com.yunche.loan.domain.query;

import lombok.Data;

@Data
public class UniversalCompensationQuery {

    private Long orderId;//业务单号

    private Long insteadPayOrderId;

}
