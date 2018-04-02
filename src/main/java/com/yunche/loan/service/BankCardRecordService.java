package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.BankCardRecordVO;
import com.yunche.loan.domain.vo.RecombinationVO;

public interface BankCardRecordService {

    public ResultBean importFile(String pathFileName);

    public ResultBean<BankCardRecordVO>  query(BankCardRecordVO bankCardRecordVO);

    public ResultBean<RecombinationVO> detail(Long orderId);

    public ResultBean  input(BankCardRecordVO bankCardRecordVO);

}
