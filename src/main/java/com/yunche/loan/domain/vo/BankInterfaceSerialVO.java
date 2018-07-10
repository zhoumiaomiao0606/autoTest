package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import lombok.Data;

@Data
public class BankInterfaceSerialVO extends BankInterfaceSerialDO{

    private String mergeStatus;//0失败 1成功

    private String electricResults;//电审状态
}
