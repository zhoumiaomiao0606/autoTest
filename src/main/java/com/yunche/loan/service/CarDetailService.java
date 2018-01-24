package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.CarDetailQuery;
import com.yunche.loan.domain.dataObj.CarDetailDO;
import com.yunche.loan.domain.viewObj.CarDetailVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface CarDetailService {

    ResultBean<CarDetailVO> getById(Long id);

    ResultBean<Long> create(CarDetailDO carDetailDO);

    ResultBean<Void> update(CarDetailDO carDetailDO);

    ResultBean<Void> delete(Long id);

    ResultBean<List<CarDetailVO>> query(CarDetailQuery query);
}
