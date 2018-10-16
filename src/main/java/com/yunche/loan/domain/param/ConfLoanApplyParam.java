package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.BankDO;
import com.yunche.loan.domain.entity.ConfLoanApplyDO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

@Data
public class ConfLoanApplyParam {

    private ConfLoanApplyDO newCar;

    private ConfLoanApplyDO oldCar;
}
