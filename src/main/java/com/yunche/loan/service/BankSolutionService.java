package com.yunche.loan.service;

import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.feign.response.ApplyStatusResponse;
import com.yunche.loan.config.feign.response.ApplycreditstatusResponse;
import com.yunche.loan.config.feign.response.CreditCardApplyResponse;
import com.yunche.loan.domain.param.BankOpenCardParam;

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
     public CreditCardApplyResponse creditcardapply(BankOpenCardParam bankOpenCardParam);

     /**
      * 查询申请进度
      */
     public ApplyStatusResponse applystatus(Long orderId);

     public ApplycreditstatusResponse applycreditstatus(ICBCApiRequest.Applycreditstatus applycreditstatus);
}
