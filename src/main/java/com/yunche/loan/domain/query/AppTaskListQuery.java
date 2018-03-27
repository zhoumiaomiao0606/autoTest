package com.yunche.loan.domain.query;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

@Data
public class AppTaskListQuery extends BaseQuery {

    @NotNull
    private Integer multipartType;

    private String customer;

    public String getCustomer() {
        if (StringUtils.isBlank(customer)) {
            return null;
        }
        return customer;
    }
}
