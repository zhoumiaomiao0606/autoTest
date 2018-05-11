package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.RepaymentRecordParam;
import com.yunche.loan.domain.vo.RepaymentRecordVO;

import java.util.List;

public interface BankRepayRecordService {


    ResultBean<List<RepaymentRecordVO>> query();

    ResultBean batchFileList(Integer pageIndex,  Integer pageSize);


    ResultBean<RepaymentRecordParam> detail(Long orderId);


    ResultBean<Void> importFile(String key);





}
