package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.BizAreaQuery;
import com.yunche.loan.domain.dataObj.BizAreaDO;
import com.yunche.loan.domain.param.BizAreaParam;
import com.yunche.loan.domain.viewObj.CascadeAreaVO;
import com.yunche.loan.domain.viewObj.BizAreaVO;
import com.yunche.loan.domain.viewObj.CascadeVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
public interface BizAreaService {
    ResultBean<Long> create(BizAreaParam bizAreaParam);

    ResultBean<Void> update(BizAreaDO bizAreaDO);

    ResultBean<Void> delete(Long id);

    ResultBean<BizAreaVO> getById(Long id);

    ResultBean<List<BizAreaVO>> query(BizAreaQuery query);

    ResultBean<List<CascadeVO>> listAll();

    ResultBean<List<CascadeAreaVO.Prov>> listArea(BizAreaQuery query);

    ResultBean<Void> bindArea(Long id, String areaIds);

    ResultBean<Void> unbindArea(Long id, String areaIds);
}
