package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.FinancialProductParam;
import com.yunche.loan.domain.query.FinancialQuery;
import com.yunche.loan.domain.entity.FinancialProductDO;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.FinancialProductAndRateVO;
import com.yunche.loan.domain.vo.FinancialProductVO;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
public interface FinancialProductService {

    ResultBean<Void> batchInsert(List<FinancialProductDO> financialProductDOs);

    ResultBean<Void> insert(FinancialProductParam FinancialProductParam);

    ResultBean<Void> update(FinancialProductParam financialProductParam);

    ResultBean<Void> delete(Long prodId);

    ResultBean<Void> disable(Long prodId);

    ResultBean<Void> enable(Long prodId);

    ResultBean<FinancialProductVO> getById(Long prodId);

    ResultBean<List<FinancialProductVO>> getByCondition(FinancialQuery financialQuery);

    ResultBean<List<BaseVO>> listByPartnerId(Long partnerId);
}
