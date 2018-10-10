package com.yunche.loan.service;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.AccommodationApplyParam;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.ExportApplyLoanPushParam;

public interface JinTouHangAccommodationApplyService {

//    ResultBean revoke(AccommodationApplyParam param);

    void dealTask(ApprovalParam param);

    ResultBean reject(AccommodationApplyParam param);

    ResultBean applyLoan(AccommodationApplyParam param);

    ResultBean batchLoan(AccommodationApplyParam param);

    ResultBean batchImp(String key);

    ResultBean export(ExportApplyLoanPushParam param);

    ResultBean detail(Long bridgeProcessId,Long orderId);

    ResultBean abnormalRepay(AccommodationApplyParam param);



    ResultBean repayInterestRegister(AccommodationApplyParam param);

    ResultBean exportJinTouHangRepayInfo(ExportApplyLoanPushParam param);


    ResultBean exportJinTouHangInterestRegister(ExportApplyLoanPushParam param);

    ResultBean calMoney(Long bridgeProcessId,Long orderId);
}
