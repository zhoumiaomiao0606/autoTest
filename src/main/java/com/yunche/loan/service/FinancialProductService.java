package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.FinancialQuery;
import com.yunche.loan.domain.dataObj.FinancialProductDO;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
public interface FinancialProductService {

    ResultBean<Void> batchInsert(List<FinancialProductDO> financialProductDOs);

    ResultBean<Void> insert(FinancialProductDO financialProductDO);

    ResultBean<Void> update(FinancialProductDO financialProductDO);

    ResultBean<FinancialProductDO> getById(Long prodId);

    ResultBean<List<FinancialProductDO>> getByCondition(FinancialQuery financialQuery);
}
