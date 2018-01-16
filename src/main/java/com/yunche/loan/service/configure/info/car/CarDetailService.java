package com.yunche.loan.service.configure.info.car;

import com.yunche.loan.obj.configure.info.car.CarDetailDO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface CarDetailService {

    Integer batchInsert(List<CarDetailDO> carSeriesDOS);

    Integer insert(CarDetailDO carDetailDO);

    List<Integer> getAllId();
}
