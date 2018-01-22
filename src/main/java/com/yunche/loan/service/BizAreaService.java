package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BizAreaQuery;
import com.yunche.loan.domain.param.BizAreaParam;
import com.yunche.loan.domain.valueObj.AreaVO;
import com.yunche.loan.domain.valueObj.BizAreaVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
public interface BizAreaService {
    ResultBean<Long> create(BizAreaParam bizAreaParam);

    ResultBean<Void> update(BizAreaParam bizAreaParam);

    ResultBean<Void> delete(Long id);

    ResultBean<BizAreaVO> getById(Long id);

    ResultBean<List<BizAreaVO>> query(BizAreaQuery query);

    ResultBean<List<AreaVO.Prov>> listCity(BizAreaQuery query);

    ResultBean<Void> deleteRelaArea(Long id, Long areaId);
}
