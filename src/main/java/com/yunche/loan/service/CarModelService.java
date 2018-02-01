package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.CarModelQuery;
import com.yunche.loan.domain.dataObj.CarModelDO;
import com.yunche.loan.domain.viewObj.CarModelVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface CarModelService {

    ResultBean<Long> create(CarModelDO carModelDO);

    /**
     * 编辑选中的(需要编辑的字段)
     *
     * @param carModelDO
     * @return
     */
    ResultBean<Void> update(CarModelDO carModelDO);

    ResultBean<Void> delete(Long id);

    ResultBean<CarModelVO> getById(Long id);

    ResultBean<List<CarModelVO>> query(CarModelQuery query);

}
