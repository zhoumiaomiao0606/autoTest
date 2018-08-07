package com.yunche.loan.domain.vo;

import com.google.common.collect.Lists;
import com.yunche.loan.domain.entity.BankDO;
import lombok.Data;

import java.util.List;

@Data
public class BankReturnVO {
    private BankDO info;

    private List<Long> list = Lists.newArrayList();
}
