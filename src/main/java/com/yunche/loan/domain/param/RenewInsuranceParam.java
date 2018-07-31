package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.RenewInsuranceDO;
import lombok.Data;

@Data
public class RenewInsuranceParam extends RenewInsuranceDO {

    private String customerName;

    private String employeeName;

    private String sms;

    private String telphone;
}
