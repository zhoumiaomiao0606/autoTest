package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.RepaymentRecordVO;

import java.util.List;

public interface BankRepayRecordService {


    ResultBean<List<RepaymentRecordVO>> query();

    ResultBean batchFileList(Integer pageIndex, Integer pageSize, String fileName, String startDate,String endDate);


    ResultBean detail(Integer pageIndex, Integer pageSize,Long orderId,String userName,String idCard, Byte isCustomer);


    ResultBean<Void> importFile(String key);


    boolean  autoImportFile(String key);





}
