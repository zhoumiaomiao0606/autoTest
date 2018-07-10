package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.vo.RecombinationVO;

public interface BankOpenCardService {


    ResultBean<RecombinationVO> detail(Long orderId);

    ResultBean openCard(BankOpenCardParam bankOpenCardParam);

    boolean importFile(String ossKey);

    ResultBean save(BankOpenCardParam bankOpenCardParam);

    ResultBean taskschedule(Long order);

}
