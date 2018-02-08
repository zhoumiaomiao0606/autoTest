package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.AreaCache;
import com.yunche.loan.domain.viewObj.CascadeAreaVO;
import com.yunche.loan.domain.viewObj.BaseAreaVO;
import com.yunche.loan.dao.mapper.BaseAreaDOMapper;
import com.yunche.loan.domain.dataObj.BaseAreaDO;
import com.yunche.loan.domain.queryObj.BaseAreaQuery;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.BaseAreaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Service
@Transactional
public class BaseAreaServiceImpl implements BaseAreaService {

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;
    @Autowired
    private AreaCache areaCache;


    @Override
    public ResultBean<BaseAreaVO> getById(Long areaId) {
        Preconditions.checkNotNull(areaId, "areaId不能为空");

        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
        Preconditions.checkNotNull(baseAreaDO, "areaId有误，数据不存在.");

        BaseAreaVO baseAreaVO = new BaseAreaVO();
        BeanUtils.copyProperties(baseAreaDO, baseAreaVO);

        return ResultBean.ofSuccess(baseAreaVO);
    }

    @Override
    public ResultBean<List<BaseAreaVO>> getByIdList(List<Long> areaIdList) {
        Preconditions.checkNotNull(areaIdList, "areaIdList不能为空");

        List<BaseAreaDO> baseAreaDOList = baseAreaDOMapper.selectByIdList(areaIdList, null);
        Preconditions.checkNotNull(baseAreaDOList, "areaId有误，数据不存在.");

        List<BaseAreaVO> baseAreaVOList = Lists.newArrayList();
        for (BaseAreaDO baseAreaDO : baseAreaDOList) {
            BaseAreaVO baseAreaVO = new BaseAreaVO();
            BeanUtils.copyProperties(baseAreaDO, baseAreaVO);
            baseAreaVOList.add(baseAreaVO);
        }

        return ResultBean.ofSuccess(baseAreaVOList);
    }

    @Override
    public ResultBean<Long> create(BaseAreaDO baseAreaDO) {
        Preconditions.checkArgument(null != baseAreaDO && null != baseAreaDO.getAreaId(), "areaId不能为空");
        Preconditions.checkNotNull(baseAreaDO.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(baseAreaDO.getStatus()) || INVALID_STATUS.equals(baseAreaDO.getStatus()),
                "状态非法");

        baseAreaDO.setGmtCreate(new Date());
        baseAreaDO.setGmtModify(new Date());
        int count = baseAreaDOMapper.insertSelective(baseAreaDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        // 刷新缓存
        areaCache.refresh();

        return ResultBean.ofSuccess(baseAreaDO.getAreaId(), "创建成功");
    }

    @Override
    public ResultBean<Void> update(BaseAreaDO baseAreaDO) {
        Preconditions.checkArgument(null != baseAreaDO && null != baseAreaDO.getAreaId(), "areaId不能为空");

        baseAreaDO.setGmtModify(new Date());
        int count = baseAreaDOMapper.updateByPrimaryKeySelective(baseAreaDO);
        Preconditions.checkArgument(count > 0, "更新失败");

        // 刷新缓存
        areaCache.refresh();

        return ResultBean.ofSuccess(null, "更新成功");
    }

    @Override
    public ResultBean<Void> delete(Long areaId) {
        Preconditions.checkNotNull(areaId, "areaId不能为空");

        int count = baseAreaDOMapper.deleteByPrimaryKey(areaId);
        Preconditions.checkArgument(count > 0, "删除失败");

        // 刷新缓存
        areaCache.refresh();

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<BaseAreaVO> query(BaseAreaQuery query) {

        List<BaseAreaDO> baseAreaDOS = baseAreaDOMapper.query(query);

        return null;
    }

    @Override
    public ResultBean<List<CascadeAreaVO>> list() {
        // 走缓存
        List<CascadeAreaVO> cascadeAreaVOList = areaCache.get();
        return ResultBean.ofSuccess(cascadeAreaVOList);
    }
}
