package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BizAreaQuery;
import com.yunche.loan.domain.dataObj.AreaVO;
import com.yunche.loan.domain.dataObj.BizAreaDO;
import com.yunche.loan.domain.valueObj.BizAreaVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
public interface BizAreaService {
    ResultBean<Long> create(BizAreaDO bizAreaDO);

    ResultBean<Void> update(BizAreaDO bizAreaDO);

    ResultBean<Void> delete(Long id);

    ResultBean<BizAreaVO> getById(Long id);

    ResultBean<List<BizAreaVO>> query(BizAreaQuery query);

    ResultBean<List<AreaVO>> listCity(BizAreaQuery query);
}
