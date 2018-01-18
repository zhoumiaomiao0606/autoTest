package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.CarBrandDOMapper;
import com.yunche.loan.domain.dataObj.CarBrandDO;
import com.yunche.loan.domain.valueObj.CarBrandVO;
import com.yunche.loan.service.CarBrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public ResultBean<Long> create(CarBrandDO carBrandDO) {
        Preconditions.checkArgument(null != carBrandDO && StringUtils.isNotBlank(carBrandDO.getName()), "品牌名称不能为空");

        // 品牌名已存在校验
        List<String> brandNameList = carBrandDOMapper.getAllName();
        Preconditions.checkArgument(!brandNameList.contains(carBrandDO.getName().trim()), "品牌名已存在");

        carBrandDO.setGmtCreate(new Date());
        carBrandDO.setGmtModify(new Date());
        int count = carBrandDOMapper.insertSelective(carBrandDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return ResultBean.ofSuccess(carBrandDO.getId());
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = carBrandDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<Void> update(CarBrandDO carBrandDO) {
        Preconditions.checkArgument(null != carBrandDO && null != carBrandDO.getId(), "id不能为空");

        carBrandDO.setGmtModify(new Date());
        int count = carBrandDOMapper.updateByPrimaryKeySelective(carBrandDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<CarBrandVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        CarBrandDO carBrandDO = carBrandDOMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(carBrandDO, "id有误，数据不存在.");

        CarBrandVO carBrandVO = new CarBrandVO();
        BeanUtils.copyProperties(carBrandDO, carBrandVO);

        return ResultBean.ofSuccess(carBrandVO);
    }

    @Override
    public ResultBean<List<CarBrandVO>> listAll() {

        List<CarBrandDO> carBrandDOS = carBrandDOMapper.getAll();

        List<CarBrandVO> carBrandVOS = carBrandDOS.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    CarBrandVO carBrandVO = new CarBrandVO();
                    BeanUtils.copyProperties(e, carBrandVO);
                    return carBrandVO;
                })
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(carBrandVOS);
    }

}
