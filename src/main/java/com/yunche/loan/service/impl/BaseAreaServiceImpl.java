package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.domain.dataObj.AreaVO;
import com.yunche.loan.domain.valueObj.BaseAreaVO;
import com.yunche.loan.dao.mapper.BaseAreaDOMapper;
import com.yunche.loan.domain.dataObj.BaseAreaDO;
import com.yunche.loan.domain.QueryObj.BaseAreaQuery;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.BaseAreaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.Collator;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.AreaConst.*;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Service
public class BaseAreaServiceImpl implements BaseAreaService {

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Override
    public ResultBean<BaseAreaVO> getById(Long areaId) {
        Preconditions.checkNotNull(areaId, "areaId不能为空");

        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId);
        Preconditions.checkNotNull(baseAreaDO, "areaId有误，数据不存在.");

        BaseAreaVO baseAreaVO = new BaseAreaVO();
        BeanUtils.copyProperties(baseAreaDO, baseAreaVO);

        return ResultBean.ofSuccess(baseAreaVO);
    }

    @Override
    public ResultBean<Long> create(BaseAreaDO baseAreaDO) {
        Preconditions.checkArgument(null != baseAreaDO && null != baseAreaDO.getAreaId(), "areaId不能为空");

        baseAreaDO.setGmtCreate(new Date());
        baseAreaDO.setGmtModify(new Date());
        int count = baseAreaDOMapper.insert(baseAreaDO);
        Preconditions.checkArgument(count > 0, "创建失败");
        return ResultBean.ofSuccess(baseAreaDO.getAreaId(), "创建成功");
    }

    @Override
    public ResultBean<Void> update(BaseAreaDO baseAreaDO) {
        Preconditions.checkArgument(null != baseAreaDO && null != baseAreaDO.getAreaId(), "areaId不能为空");

        baseAreaDO.setGmtModify(new Date());
        int count = baseAreaDOMapper.updateByPrimaryKeySelective(baseAreaDO);
        Preconditions.checkArgument(count > 0, "更新失败");
        return ResultBean.ofSuccess(null, "更新成功");
    }

    @Override
    public ResultBean<Void> delete(Long areaId) {
        Preconditions.checkNotNull(areaId, "areaId不能为空");

        int count = baseAreaDOMapper.deleteByPrimaryKey(areaId);
        Preconditions.checkArgument(count > 0, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<BaseAreaVO> query(BaseAreaQuery query) {

        List<BaseAreaDO> baseAreaDOS = baseAreaDOMapper.query(query);

        return null;
    }

    @Override
    public ResultBean<List<AreaVO>> list() {

        List<BaseAreaDO> allArea = baseAreaDOMapper.getAll();
        if (CollectionUtils.isEmpty(allArea)) {
            return ResultBean.ofSuccess(null);
        }

        // 省-市映射
        ConcurrentMap<Long, AreaVO> provCityMap = Maps.newConcurrentMap();

        // 省（全国）
        allArea.parallelStream()
                .filter(e -> null != e && null != e.getAreaId() && LEVEL_COUNTRY.equals(e.getLevel()) || LEVEL_PROV.equals(e.getLevel()))
                .forEach(e -> {

                    if (!provCityMap.containsKey(e.getAreaId())) {

                        AreaVO areaVO = new AreaVO();
                        areaVO.setId(e.getAreaId());
                        areaVO.setName(e.getAreaName());
                        areaVO.setLevel(Integer.valueOf(e.getLevel()));
                        areaVO.setCityList(Lists.newArrayList());

                        provCityMap.put(e.getAreaId(), areaVO);
                    }

                });

        // 市
        allArea.stream()
                .filter(e -> null != e && null != e.getAreaId() && LEVEL_CITY.equals(e.getLevel()))
                .forEach(e -> {

                    if (provCityMap.containsKey(e.getParentAreaId())) {

                        AreaVO.City city = new AreaVO.City();
                        city.setId(e.getAreaId());
                        city.setName(e.getAreaName());
                        city.setLevel(Integer.valueOf(e.getLevel()));

                        provCityMap.get(e.getParentAreaId()).getCityList().add(city);
                    }

                });

        // 根据中文拼音排序
        Comparator<Object> CHINA_COMPARE = Collator.getInstance(java.util.Locale.CHINA);

        List<AreaVO> tmpAreaVOList = Lists.newArrayList();
        List<AreaVO> areaVOList = Lists.newArrayList();

        provCityMap.values().stream()
                .sorted(Comparator.comparing(AreaVO::getName, CHINA_COMPARE))
                .forEach(e -> {
                    // 全国拿出来放到第一位
                    if (COUNTRY.equals(e.getName())) {
                        areaVOList.add(e);
                    } else {
                        tmpAreaVOList.add(e);
                    }
                });
        areaVOList.addAll(tmpAreaVOList);

        return ResultBean.ofSuccess(areaVOList);
    }


}
