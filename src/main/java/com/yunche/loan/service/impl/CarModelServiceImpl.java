package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.CarBrandDOMapper;
import com.yunche.loan.dao.mapper.CarDetailDOMapper;
import com.yunche.loan.dao.mapper.CarModelDOMapper;
import com.yunche.loan.domain.QueryObj.CarModelQuery;
import com.yunche.loan.domain.dataObj.CarBrandDO;
import com.yunche.loan.domain.dataObj.CarDetailDO;
import com.yunche.loan.domain.dataObj.CarModelDO;
import com.yunche.loan.domain.viewObj.CarModelVO;
import com.yunche.loan.service.CarModelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
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
public class CarModelServiceImpl implements CarModelService {

    @Autowired
    private CarBrandDOMapper carBrandDOMapper;
    @Autowired
    private CarModelDOMapper carModelDOMapper;
    @Autowired
    private CarDetailDOMapper carDetailDOMapperl;


    @Override
    public ResultBean<Long> create(CarModelDO carModelDO) {
        Preconditions.checkArgument(null != carModelDO && null != carModelDO.getBrandId(), "所属品牌不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(carModelDO.getFullName()), "车系名称不能为空");
        Preconditions.checkNotNull(carModelDO.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(carModelDO.getStatus()) || INVALID_STATUS.equals(carModelDO.getStatus()),
                "状态非法");

        // name校验
        List<String> modelNameList = carModelDOMapper.getNameListByBrandId(carModelDO.getBrandId(), VALID_STATUS);
        Preconditions.checkArgument(!modelNameList.contains(carModelDO.getFullName().trim()), "当前品牌下，此车系名称已存在");

        carModelDO.setGmtCreate(new Date());
        carModelDO.setGmtModify(new Date());
        int count = carModelDOMapper.insertSelective(carModelDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return ResultBean.ofSuccess(carModelDO.getId());
    }

    @Override
    public ResultBean<Void> update(CarModelDO carModelDO) {
        Preconditions.checkArgument(null != carModelDO && null != carModelDO.getId(), "id不能为空");

        // 校验是否是删除操作
        checkIfDel(carModelDO);

        carModelDO.setGmtModify(new Date());
        int count = carModelDOMapper.updateByPrimaryKeySelective(carModelDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        // 校验是否存在子车型
        checkHasChilds(id);

        CarModelDO carModelDO = new CarModelDO();
        carModelDO.setGmtModify(new Date());
        int count = carModelDOMapper.updateByPrimaryKeySelective(carModelDO);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<CarModelVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        CarModelDO carModelDO = carModelDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        Preconditions.checkNotNull(carModelDO, "id有误，数据不存在.");

        CarModelVO carModelVO = new CarModelVO();
        BeanUtils.copyProperties(carModelDO, carModelVO);

        // 补充品牌名称
        Long brandId = carModelVO.getBrandId();
        if (null != brandId) {
            CarBrandDO carBrandDO = carBrandDOMapper.selectByPrimaryKey(brandId, VALID_STATUS);
            if (null != carBrandDO) {
                carModelVO.setBrandName(carBrandDO.getName());
            }
        }

        return ResultBean.ofSuccess(carModelVO);
    }

    @Override
    public ResultBean<List<CarModelVO>> query(CarModelQuery query) {
        int totalNum = carModelDOMapper.count(query);
        if (totalNum < 1) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

        // 根据座位数、生产方式、品牌查询
        List<CarModelDO> carModelDOS = carModelDOMapper.query(query);
        if (CollectionUtils.isEmpty(carModelDOS)) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

        List<CarModelVO> carModelVOS = carModelDOS.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    CarModelVO carModelVO = new CarModelVO();
                    BeanUtils.copyProperties(e, carModelVO);
                    return carModelVO;
                })
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(carModelVOS, totalNum, query.getPageIndex(), query.getPageSize());
    }

    /**
     * 校验是否是删除操作
     *
     * @param carModelDO
     */
    private void checkIfDel(CarModelDO carModelDO) {
        if (INVALID_STATUS.equals(carModelDO.getStatus())) {
            // 校验是否存在子级区域
            checkHasChilds(carModelDO.getId());
        }
    }

    /**
     * 校验是否存在子车型
     *
     * @param modelId
     */
    private void checkHasChilds(Long modelId) {
        List<CarDetailDO> carDetailDOS = carDetailDOMapperl.getDetailListByModelId(modelId, VALID_STATUS);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(carDetailDOS), "请先删除所有子车型");
    }

}
