package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanDataFlowDO;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.UniversalDataFlowDetailVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/7/4
 */
public interface LoanDataFlowService {

    LoanDataFlowDO getLastByOrderIdAndType(Long orderId, Byte oldType);

    ResultBean<UniversalDataFlowDetailVO> detail(Long id);

    ResultBean create(LoanDataFlowDO loanDataFlowDO);

    ResultBean update(LoanDataFlowDO loanDataFlowDO);

    ResultBean<List<BaseVO>> flowDept();

    ResultBean<Object> key();

    ResultBean<Object> key_get_type(String key);

    ResultBean<Object> type_get_key(String type);
}