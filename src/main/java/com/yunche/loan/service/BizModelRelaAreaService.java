package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BizModelQuery;
import com.yunche.loan.domain.dataObj.BizModelRelaAreaDO;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.domain.viewObj.BizModelVO;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
public interface BizModelRelaAreaService {

    ResultBean<Void> insert(BizModelRelaAreaDO bizModelRelaAreaDO);

    ResultBean<Void> BatchInsert(List<BizModelRelaAreaDO> bizModelRelaAreaDOList);

    ResultBean<Void> update(BizModelRelaAreaDO bizModelRelaAreaDO);

    ResultBean<Void> delete(Long bizId);

    ResultBean<List<BizModelRelaAreaDO>> getById(Long bizId);

}
