package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.dao.mapper.CarDetailDOMapper;
import com.yunche.loan.domain.dataObj.CarDetailDO;
import com.yunche.loan.service.CarDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Service
public class CarDetailServiceImpl implements CarDetailService {

    @Autowired
    private CarDetailDOMapper carDetailDOMapper;

    @Override
    @Transactional
    public Integer batchInsert(List<CarDetailDO> carSeriesDOS) {
        int count = carDetailDOMapper.batchInsert(carSeriesDOS);
        Preconditions.checkArgument(count > 0, "批量插入失败");
        return count;
    }

    @Override
    @Transactional
    public Integer insert(CarDetailDO carDetailDO) {
        int count = carDetailDOMapper.insert(carDetailDO);
        Preconditions.checkArgument(count > 0, "插入失败");
        return count;
    }

    @Override
    public List<Integer> getAllId() {
        List<Integer> allId = carDetailDOMapper.getAllId();
        return allId;
    }

    @Override
    public CarDetailDO getById(Integer id) {
        Preconditions.checkNotNull(id, "id不能为空");
        CarDetailDO carDetailDO = carDetailDOMapper.selectByPrimaryKey(id);
        return carDetailDO;
    }

    @Override
    public List<CarDetailDO> getAllIdAndModelId() {
        List<CarDetailDO> carDetailDOS = carDetailDOMapper.getAllIdAndModelId();
        return carDetailDOS;
    }
}
