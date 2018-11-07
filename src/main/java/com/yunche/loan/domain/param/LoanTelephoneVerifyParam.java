package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.LoanTelephoneVerifyDO;
import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/4/12
 */
@Data
public class LoanTelephoneVerifyParam extends LoanTelephoneVerifyDO {

    //签单类型
    private Byte signatureType;
}
