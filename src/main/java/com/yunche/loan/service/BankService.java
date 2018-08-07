package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankDO;
import com.yunche.loan.domain.param.BankParam;
import com.yunche.loan.domain.query.BankQuery;
import com.yunche.loan.domain.vo.BankVO;
import com.yunche.loan.domain.vo.CascadeAreaVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/5/15
 */
public interface BankService {
    ResultBean<List<String>> listAll();

    ResultBean<List<BankDO>> query(BankQuery query);

    ResultBean<Long> create(BankParam bankParam);

    ResultBean<Void> update(BankParam bankParam);

    ResultBean<Void> delete(Long id);

    ResultBean<BankVO> getById(Long id);

    ResultBean<List<Long>> areaListByBankName(String bankName);

    ResultBean<List<CascadeAreaVO>> areaNameListByBankName(String bankName);
}
