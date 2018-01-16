package com.yunche.loan.service.impl.configure.info.car;

import com.google.common.base.Preconditions;
import com.yunche.loan.mapper.configure.info.car.CarModelDOMapper;
import com.yunche.loan.obj.configure.info.car.CarModelDO;
import com.yunche.loan.service.configure.info.car.CarModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Service
public class CarModelServiceImpl implements CarModelService {

    @Autowired
    private CarModelDOMapper carModelDOMapper;


    @Override
    public Integer batchInsert(List<CarModelDO> carModelDOS) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(carModelDOS), "插入数据不能为空");
        Integer count = carModelDOMapper.batchInsert(carModelDOS);
        Preconditions.checkArgument(count > 0, "批量插入失败");
        return count;
    }

    @Override
    public List<Integer> getAllId() {
        List<Integer> allId = carModelDOMapper.getAllId();
        return allId;
    }

    @Override
    public Integer updateSelective(CarModelDO carModelDO) {
        Preconditions.checkArgument(null != carModelDO && null != carModelDO.getId(), "id不能为空");
        int count = carModelDOMapper.updateByPrimaryKeySelective(carModelDO);
        Preconditions.checkArgument(count > 0, "编辑失败");
        return count;
    }
}
