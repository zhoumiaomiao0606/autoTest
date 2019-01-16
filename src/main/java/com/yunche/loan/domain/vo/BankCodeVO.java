package com.yunche.loan.domain.vo;

import com.google.common.collect.Lists;
import com.yunche.loan.domain.entity.BankCodeDO;
import lombok.Data;

import java.util.List;

@Data
public class BankCodeVO
{
    private Integer id;

    private String code;

    private String name;

    private List<BankCodeDO>  list = Lists.newArrayList();
}
