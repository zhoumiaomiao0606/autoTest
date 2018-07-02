package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.BankOpenCardParam;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface BankSolutionService {
     //自动征信
     void creditAutomaticCommit(@Validated @NotNull  Long orderId,@Validated @NotNull  Long bankId);
     //人工补偿
     void creditArtificialCompensation(@Validated @NotNull  Long orderId,@Validated @NotNull  Long bankId,@Validated @NotNull Long customerId);


     /**
      * 银行开卡
      * @param bankOpenCardParam
      * @return
      */
     public  void creditcardapply(BankOpenCardParam bankOpenCardParam);
}
