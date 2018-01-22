package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BizModelQuery;
import com.yunche.loan.domain.QueryObj.FinancialQuery;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.domain.viewObj.BizModelVO;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
public interface BizModelService {

    ResultBean<Void> insert(BizModelVO bizModelVO);

    ResultBean<Void> update(BizModelVO bizModelVO);

    ResultBean<Void> delete(Long bizId);

    ResultBean<FinancialProductDO> getById(Long bizId);

    ResultBean<List<FinancialProductDO>> getByCondition(BizModelQuery bizModelQuery);
}
