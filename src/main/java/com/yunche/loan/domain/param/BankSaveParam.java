package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.BankDO;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class BankSaveParam {
    private BankDO bank;
    //绑定上牌地list
    private List<Long>  bankCarLicenseLocationList = Collections.EMPTY_LIST;
}
