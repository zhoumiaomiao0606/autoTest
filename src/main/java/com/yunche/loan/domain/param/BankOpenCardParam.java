package com.yunche.loan.domain.param;


import com.yunche.loan.config.feign.request.ICBCApiRequest;
import lombok.Data;
@Data
public class BankOpenCardParam  extends ICBCApiRequest.ApplyBankOpenCard{
        private Long customerId;
}
