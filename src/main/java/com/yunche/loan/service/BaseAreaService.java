package com.yunche.loan.service;

import com.yunche.loan.domain.vo.CascadeAreaVO;
import com.yunche.loan.domain.vo.BaseAreaVO;
import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.query.BaseAreaQuery;
import com.yunche.loan.config.result.ResultBean;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface BaseAreaService {

    ResultBean<BaseAreaVO> getById(Long areaId);

    ResultBean<List<BaseAreaVO>> getByIdList(List<Long> areaIdList);

    ResultBean<Long> create(BaseAreaDO baseAreaDO);

    ResultBean<Void> update(BaseAreaDO baseAreaBO);

    ResultBean<Void> delete(Long areaId);

    ResultBean<BaseAreaVO> query(BaseAreaQuery queryJObj);

    ResultBean<List<CascadeAreaVO>> list();

    ResultBean<String> getFullAreaName(Long areaId);

    ResultBean<List<CascadeAreaVO>> getApplyLicensePlateArea(Long partnerId);
}
