package com.yunche.loan.service;

import com.yunche.loan.domain.dataObj.CarModelDO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface CarModelService {
    /**
     * 批量插入
     *
     * @param carModelDOS
     * @return
     */
    Integer batchInsert(List<CarModelDO> carModelDOS);

    /**
     * 获取所有ID
     *
     * @return
     */
    List<Long> getAllId();

    /**
     * 编辑选中的(需要编辑的字段)
     *
     * @param updateCarModelDO
     * @return
     */
    Integer updateSelective(CarModelDO updateCarModelDO);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    CarModelDO getById(Long id);
}
