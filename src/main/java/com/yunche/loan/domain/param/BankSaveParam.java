package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.BankDO;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Data
public class BankSaveParam {

    private BankDO bank;

    /**
     * 绑定上牌地ID列表    最后一级ID  --> cityId / areaId
     */
    private Set<Long> bankCarLicenseLocationList = Collections.EMPTY_SET;
}
