package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.dataObj.BizModelRelaFinancialProdDO;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
public interface BizModelRelaFinancialProdService {

    ResultBean<Void> insert(BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO);

    ResultBean<Void> batchInsert(List<BizModelRelaFinancialProdDO> bizModelRelaAreaDOList);

    ResultBean<Void> update(BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO);

    ResultBean<Void> delete(Long bizId);

    ResultBean<List<BizModelRelaFinancialProdDO>> getById(Long bizId);

}
