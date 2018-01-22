package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.dataObj.CarBrandDO;
import com.yunche.loan.domain.viewObj.CarBrandVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface CarBrandService {

    ResultBean<Long> create(CarBrandDO carBrandDO);

    ResultBean<Void> update(CarBrandDO carBrandDO);

    ResultBean<Void> delete(Long id);

    ResultBean<CarBrandVO> getById(Long id);

    ResultBean<List<CarBrandVO>> listAll();
}
