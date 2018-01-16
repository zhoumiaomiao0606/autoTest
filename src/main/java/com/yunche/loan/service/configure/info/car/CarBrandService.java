package com.yunche.loan.service.configure.info.car;

import com.yunche.loan.obj.configure.info.car.CarBrandDO;

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
