package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BizModelRelaFinancialProdDO;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
public interface BizModelRelaFinancialProdService {

    ResultBean<Void> insert(BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO);

    ResultBean<Void> batchInsert(List<BizModelRelaFinancialProdDO> bizModelRelaAreaDOList);

    ResultBean<Void> update(BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO);

    ResultBean<Void> batchUpdate(List<BizModelRelaFinancialProdDO> bizModelRelaAreaDOList);

    ResultBean<Void> delete(Long bizId);

    ResultBean<Void> deleteRelaFinancialProd(Long bizId, Long prodId);

    ResultBean<Void> addRelaFinancialProd(Long bizId, Long prodId);

    ResultBean<List<BizModelRelaFinancialProdDO>> getById(Long bizId);

}
