package com.yunche.loan.service;

import com.yunche.loan.domain.dataObj.CarBrandDO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface CarBrandService {

    Integer batchInsert(List<CarBrandDO> carBrandDOS);

    Integer insert(CarBrandDO carBrandDO);

    List<Integer> getAllId();
}
