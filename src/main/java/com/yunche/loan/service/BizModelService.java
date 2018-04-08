package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.BizModelParam;
import com.yunche.loan.domain.query.BizModelQuery;
import com.yunche.loan.domain.vo.BizModelVO;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
public interface BizModelService {

    ResultBean<Long> insert(BizModelParam bizModelParam);

    ResultBean<Void> update(BizModelParam bizModelParam);

    ResultBean<Void> delete(Long bizId);

    ResultBean<Void> disable(Long bizId);

    ResultBean<Void> enable(Long bizId);

    ResultBean<BizModelVO> getById(Long bizId);

    ResultBean<List<BizModelVO>> getByCondition(BizModelQuery bizModelQuery);
}
