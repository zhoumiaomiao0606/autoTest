package com.yunche.loan.service;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.AccommodationApplyParam;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.ExportApplyLoanPushParam;

import java.util.Date;

public interface JinTouHangAccommodationApplyService {

//    ResultBean revoke(AccommodationApplyParam param);

    void dealTask(ApprovalParam param);

    ResultBean reject(AccommodationApplyParam param);

    ResultBean applyLoan(AccommodationApplyParam param);

    ResultBean applyOldLoan(AccommodationApplyParam param);

    ResultBean batchLoan(AccommodationApplyParam param);

    ResultBean batchImp(String key);

    ResultBean export(ExportApplyLoanPushParam param);

    ResultBean errorExport(ExportApplyLoanPushParam param);

    ResultBean detail(Long bridgeProcessId,Long orderId);

    ResultBean abnormalRepay(AccommodationApplyParam param);



    ResultBean repayInterestRegister(AccommodationApplyParam param);

    ResultBean exportJinTouHangRepayInfo(ExportApplyLoanPushParam param);


    ResultBean exportJinTouHangInterestRegister(ExportApplyLoanPushParam param);

    ResultBean calMoneyDetail(Long bridgeProcessId,Long orderId,String repayDate,String flag);

    ResultBean calMoney(Long bridgeProcessId,Long orderId,String repayDate);

    ResultBean isReturn(Long bridgeProcessId,Long orderId);

    String jtxResult(String param);

    ResultBean getBankCard(Long orderId);

    ResultBean batchEnd(AccommodationApplyParam accommodationApplyParam);
}
