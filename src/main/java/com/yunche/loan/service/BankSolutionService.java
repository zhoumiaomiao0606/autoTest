package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.BankOpenCardParam;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface BankSolutionService {
     //自动征信
     void creditAutomaticCommit(@Validated @NotNull  Long orderId,@Validated @NotNull  Long bankId,@Validated @NotNull List<LoanCustomerDO> customers);
     //人工补偿
<<<<<<< HEAD
     void creditArtificialCompensation(@Validated @NotNull  Long orderId,@Validated @NotNull  Long bankId,@Validated @NotNull Long customerId);
=======
     void creditArtificialCompensation(@Validated @NotNull  Long bankId,@Validated @NotNull Long customerId);

     /**
      * 银行开卡
      * @param bankOpenCardParam
      * @return
      */
     public  ResultBean creditcardapply(BankOpenCardParam bankOpenCardParam);
>>>>>>> 26787bd0eace9ff9c42dd3edc4e275c20c03b27a
}
