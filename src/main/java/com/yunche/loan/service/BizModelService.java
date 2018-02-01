package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.BizModelQuery;
import com.yunche.loan.domain.viewObj.BizModelVO;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
public interface BizModelService {

    ResultBean<Void> insert(BizModelVO bizModelVO);

    ResultBean<Void> update(BizModelVO bizModelVO);

    ResultBean<Void> delete(Long bizId);

    ResultBean<BizModelVO> getById(Long bizId);

    ResultBean<List<BizModelVO>> getByCondition(BizModelQuery bizModelQuery);
}
