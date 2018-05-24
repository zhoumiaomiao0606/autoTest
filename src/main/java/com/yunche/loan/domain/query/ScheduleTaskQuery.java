package com.yunche.loan.domain.query;

import lombok.Data;
@Data
public class ScheduleTaskQuery {
    String key;

    Long employeeId;

    Long telephoneVerifyLevel;

    Long collectionLevel;

    Long financeLevel;

    Long maxGroupLevel;
}
