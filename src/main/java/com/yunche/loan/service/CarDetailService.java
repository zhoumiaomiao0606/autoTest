package com.yunche.loan.service;

import com.yunche.loan.domain.dataObj.CarDetailDO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface CarDetailService {

    Integer batchInsert(List<CarDetailDO> carSeriesDOS);

    Integer insert(CarDetailDO carDetailDO);

    List<Integer> getAllId();

    CarDetailDO getById(Integer id);

    /**
     * 获取所有的 detail_id（ID）--- model_id   仅获取这两个字段
     *
     * @return
     */
    List<CarDetailDO> getAllIdAndModelId();
}
