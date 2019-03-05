package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class OrderListParam
{
    private List<Long> orderIds;
}
