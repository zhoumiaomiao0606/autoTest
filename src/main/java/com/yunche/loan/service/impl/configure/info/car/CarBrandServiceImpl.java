package com.yunche.loan.service.impl.configure.info.car;

import com.google.common.base.Preconditions;
import com.yunche.loan.mapper.configure.info.car.CarBrandDOMapper;
import com.yunche.loan.obj.configure.info.car.CarBrandDO;
import com.yunche.loan.service.configure.info.car.CarBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Service
@Transactional
public class CarBrandServiceImpl implements CarBrandService {

    @Autowired
    private CarBrandDOMapper carBrandDOMapper;

    @Override
    public Integer batchInsert(List<CarBrandDO> carBrandDOS) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(carBrandDOS), "插入数据不能为空");
        int count = carBrandDOMapper.batchInsert(carBrandDOS);
        Preconditions.checkArgument(count > 0, "批量插入失败");
        return count;
    }

    @Override
    public Integer insert(CarBrandDO carBrandDO) {
        Preconditions.checkNotNull(carBrandDO, "插入数据不能为空");
        int count = carBrandDOMapper.insert(carBrandDO);
        Preconditions.checkArgument(count > 0, "插入失败");
        return count;
    }

    @Override
    public List<Integer> getAllId() {
        List<Integer> allId = carBrandDOMapper.getAllId();
        return allId;
    }
}
