package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.RecombinationVO;

public interface BankOpenCardService {


    ResultBean<RecombinationVO> detail(Long orderId);
}
