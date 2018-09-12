package com.yunche.loan.service;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.AccommodationApplyParam;
import com.yunche.loan.domain.param.ExportApplyLoanPushParam;

public interface JinTouHangAccommodationApplyService {

    ResultBean applyLoan(AccommodationApplyParam param);

    ResultBean batchLoan(AccommodationApplyParam param);

    ResultBean export(ExportApplyLoanPushParam param);

    ResultBean detail(Long orderId);

    ResultBean abnormalRepay(AccommodationApplyParam param);



    ResultBean repayInterestRegister(AccommodationApplyParam param);

    ResultBean exportJinTouHangRepayInfo(ExportApplyLoanPushParam param);


    ResultBean exportJinTouHangInterestRegister(ExportApplyLoanPushParam param);


}
