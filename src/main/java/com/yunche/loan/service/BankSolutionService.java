package com.yunche.loan.service;

import com.yunche.loan.domain.entity.LoanCustomerDO;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface BankSolutionService {
     //自动征信
     void creditAutomaticCommit(@Validated @NotNull  Long bankId, @Validated @NotNull List<LoanCustomerDO> customers);
     //人工补偿
     void creditArtificialCompensation(@Validated @NotNull  Long bankId,@Validated @NotNull Long customerId);
}
