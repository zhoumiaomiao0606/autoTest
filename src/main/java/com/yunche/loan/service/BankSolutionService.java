package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.BankOpenCardParam;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface BankSolutionService {
     //自动征信
     void creditAutomaticCommit(Long orderId);
     //通用业务申请接口
     void commonBusinessApply(Long orderId);

     /**
      * 银行开卡
      * @param bankOpenCardParam
      * @return
      */
     public  void creditcardapply(BankOpenCardParam bankOpenCardParam);
}
