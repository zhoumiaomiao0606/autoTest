package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.BankCodeDO;
import lombok.Data;

import java.util.List;

@Data
public class InsOrUpBankListParam
{
    private Integer parentId;

    private String code;

    private String name;

    private Byte level;

    private List<BankCodeDO> childList;
}
