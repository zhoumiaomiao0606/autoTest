package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.CarBrandDOMapper;
import com.yunche.loan.dao.CarModelDOMapper;
import com.yunche.loan.domain.entity.CarBrandDO;
import com.yunche.loan.domain.entity.CarModelDO;
import com.yunche.loan.domain.vo.CarBrandVO;
import com.yunche.loan.service.CarBrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Service
@Transactional
public class CarBrandServiceImpl implements CarBrandService {

    @Autowired
    private CarBrandDOMapper carBrandDOMapper;
    @Autowired
    private CarModelDOMapper carModelDOMapper;


    @Override
    public ResultBean<Long> create(CarBrandDO carBrandDO) {
        Preconditions.checkArgument(null != carBrandDO && StringUtils.isNotBlank(carBrandDO.getName()), "品牌名称不能为空");
        Preconditions.checkNotNull(carBrandDO.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(carBrandDO.getStatus()) || INVALID_STATUS.equals(carBrandDO.getStatus()),
                "状态非法");

        // 品牌名已存在校验
        List<String> brandNameList = carBrandDOMapper.getAllName(VALID_STATUS);
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

        // 校验是否存在子车系
        checkHasChilds(id);

        CarBrandDO carBrandDO = new CarBrandDO();
        carBrandDO.setStatus(INVALID_STATUS);
        carBrandDO.setGmtModify(new Date());
        int count = carBrandDOMapper.updateByPrimaryKeySelective(carBrandDO);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<Void> update(CarBrandDO carBrandDO) {
        Preconditions.checkArgument(null != carBrandDO && null != carBrandDO.getId(), "id不能为空");

        // 校验是否是删除操作
        checkIfDel(carBrandDO);

        carBrandDO.setGmtModify(new Date());
        int count = carBrandDOMapper.updateByPrimaryKeySelective(carBrandDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<CarBrandVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        CarBrandDO carBrandDO = carBrandDOMapper.selectByPrimaryKey(id, null);
        Preconditions.checkNotNull(carBrandDO, "id有误，数据不存在.");

        CarBrandVO carBrandVO = new CarBrandVO();
        BeanUtils.copyProperties(carBrandDO, carBrandVO);

        return ResultBean.ofSuccess(carBrandVO);
    }

    @Override
    public ResultBean<List<CarBrandVO>> listAll() {

        List<CarBrandDO> carBrandDOS = carBrandDOMapper.getAll(VALID_STATUS);

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

    /**
     * 校验是否是删除操作
     *
     * @param carBrandDO
     */
    private void checkIfDel(CarBrandDO carBrandDO) {
        if (INVALID_STATUS.equals(carBrandDO.getStatus())) {
            // 校验是否存在子级区域
            checkHasChilds(carBrandDO.getId());
        }
    }

    /**
     * 校验是否存在子车系
     *
     * @param brandId
     */
    private void checkHasChilds(Long brandId) {
        List<CarModelDO> carModelDOS = carModelDOMapper.getModelListByBrandId(brandId, VALID_STATUS);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(carModelDOS), "请先删除所有子车系");
    }

}
