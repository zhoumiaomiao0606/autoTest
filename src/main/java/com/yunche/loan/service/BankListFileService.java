package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;

public interface BankListFileService {

    ResultBean batchFileList(Integer pageIndex, Integer pageSize, String fileName, String startDate, String endDate,String fileType);


    ResultBean detail(Integer pageIndex, Integer pageSize,Long orderId,String userName,String idCard, Byte isCustomer);

}
