package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.FinancialQuery;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.domain.viewObj.FinancialProductVO;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
public interface FinancialProductService {

    ResultBean<Void> batchInsert(List<FinancialProductDO> financialProductDOs);

    ResultBean<Void> insert(FinancialProductDO financialProductDO);

    ResultBean<Void> update(FinancialProductDO financialProductDO);

    ResultBean<Void> delete(Long prodId);

    ResultBean<Void> disable(Long prodId);

    ResultBean<Void> enable(Long prodId);

    ResultBean<FinancialProductVO> getById(Long prodId);

    ResultBean<List<FinancialProductVO>> getByCondition(FinancialQuery financialQuery);
}
