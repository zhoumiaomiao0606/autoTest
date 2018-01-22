package com.yunche.loan.service;

import com.yunche.loan.domain.viewObj.AreaVO;
import com.yunche.loan.domain.viewObj.BaseAreaVO;
import com.yunche.loan.domain.dataObj.BaseAreaDO;
import com.yunche.loan.domain.QueryObj.BaseAreaQuery;
import com.yunche.loan.config.result.ResultBean;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface BaseAreaService {

    ResultBean<BaseAreaVO> getById(Long areaId);

    ResultBean<Long> create(BaseAreaDO baseAreaDO);

    ResultBean<Void> update(BaseAreaDO baseAreaBO);

    ResultBean<Void> delete(Long areaId);

    ResultBean<BaseAreaVO> query(BaseAreaQuery queryJObj);

    ResultBean<List<AreaVO>> list();
}
