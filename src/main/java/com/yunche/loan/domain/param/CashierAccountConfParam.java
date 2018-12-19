package com.yunche.loan.domain.param;

import com.google.common.collect.Lists;
import com.yunche.loan.domain.entity.CashierAccountConfDO;
import lombok.Data;

import java.util.List;

@Data
public class CashierAccountConfParam
{
    private Long employeeId;

    private List<CashierAccountConfDO> list =Lists.newArrayList();
}
