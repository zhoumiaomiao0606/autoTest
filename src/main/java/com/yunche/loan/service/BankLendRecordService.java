package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankLendRecordDO;
import com.yunche.loan.domain.vo.BankLendRecordVO;
import com.yunche.loan.domain.vo.RecombinationVO;

public interface BankLendRecordService {

    //查询使用通用接口

    //明细界面查询 关联bank_loan_record表展示银行放款日期和放款金额
    ResultBean<RecombinationVO> detail(Long orderId);

    ResultBean<Void> importFile(String pathFileName);

    ResultBean<Void>  manualInput(BankLendRecordVO bankLendRecordVO);

    ResultBean<BankLendRecordDO> querySave(Long orderId);

}
