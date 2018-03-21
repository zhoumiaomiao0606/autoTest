package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.RepaymentRecordDOKey;
import com.yunche.loan.domain.vo.RepaymentRecordVO;

public interface RepaymentRecordService {


    ResultBean<RepaymentRecordVO> query(RepaymentRecordDOKey  repaymentRecordDOKey);


    ResultBean<Void> importFile(String pathFileName);





}
