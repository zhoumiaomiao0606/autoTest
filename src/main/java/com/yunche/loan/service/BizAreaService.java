package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.BizAreaQuery;
import com.yunche.loan.domain.entity.BizAreaDO;
import com.yunche.loan.domain.param.BizAreaParam;
import com.yunche.loan.domain.vo.CascadeAreaVO;
import com.yunche.loan.domain.vo.BizAreaVO;
import com.yunche.loan.domain.vo.CascadeVO;

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

    List<List<Long>> selectedList(List<Long> bizAreaIds);

    ResultBean<List<CascadeAreaVO.Partner>> listPartner(Long id);

    ResultBean<Void> bindPartner(Long id, List<Long> partnerIds);

    ResultBean<Void> unbindPartner(Long id,Long partnerId);
}
