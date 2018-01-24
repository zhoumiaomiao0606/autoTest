package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.CarDetailDOMapper;
import com.yunche.loan.domain.QueryObj.CarDetailQuery;
import com.yunche.loan.domain.dataObj.CarDetailDO;
import com.yunche.loan.domain.viewObj.CarDetailVO;
import com.yunche.loan.service.CarDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Service
@Transactional
public class CarDetailServiceImpl implements CarDetailService {

    @Autowired
    private CarDetailDOMapper carDetailDOMapper;


    @Override
    public ResultBean<CarDetailVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");
        CarDetailDO carDetailDO = carDetailDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        Preconditions.checkNotNull(carDetailDO, "id有误，数据不存在.");

        CarDetailVO carDetailVO = new CarDetailVO();
        BeanUtils.copyProperties(carDetailDO, carDetailVO);

        return ResultBean.ofSuccess(carDetailVO);
    }

    @Override
    public ResultBean<Long> create(CarDetailDO carDetailDO) {
        Preconditions.checkArgument(null != carDetailDO && null != carDetailDO.getModelId(), "车系不能为空");

        carDetailDO.setStatus(VALID_STATUS);
        carDetailDO.setGmtCreate(new Date());
        carDetailDO.setGmtModify(new Date());
        int count = carDetailDOMapper.insertSelective(carDetailDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return ResultBean.ofSuccess(carDetailDO.getId());
    }

    @Override
    public ResultBean<Void> update(CarDetailDO carDetailDO) {
        Preconditions.checkArgument(null != carDetailDO && null != carDetailDO.getId(), "id不能为空");

        carDetailDO.setGmtModify(new Date());
        int count = carDetailDOMapper.updateByPrimaryKeySelective(carDetailDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = carDetailDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<List<CarDetailVO>> query(CarDetailQuery query) {
        int totalNum = carDetailDOMapper.count(query);
        if (totalNum < 1) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

        List<CarDetailDO> carDetailDOS = carDetailDOMapper.query(query);
        if (CollectionUtils.isEmpty(carDetailDOS)) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

        List<CarDetailVO> carDetailVOS = carDetailDOS.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    CarDetailVO carDetailVO = new CarDetailVO();
                    BeanUtils.copyProperties(e, carDetailVO);
                    return carDetailVO;
                })
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(carDetailVOS, totalNum, query.getPageIndex(), query.getPageSize());
    }
}
