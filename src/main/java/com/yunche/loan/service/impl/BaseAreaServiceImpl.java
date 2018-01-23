package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.domain.viewObj.AreaVO;
import com.yunche.loan.domain.viewObj.BaseAreaVO;
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

import static com.yunche.loan.config.constant.AreaConst.*;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

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

        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
        Preconditions.checkNotNull(baseAreaDO, "areaId有误，数据不存在.");

        BaseAreaVO baseAreaVO = new BaseAreaVO();
        BeanUtils.copyProperties(baseAreaDO, baseAreaVO);

        return ResultBean.ofSuccess(baseAreaVO);
    }

    @Override
    public ResultBean<List<BaseAreaVO>> getByIdList(List<Long> areaIdList) {
        Preconditions.checkNotNull(areaIdList, "areaIdList不能为空");

        List<BaseAreaDO> baseAreaDOList = baseAreaDOMapper.selectByIdList(areaIdList, VALID_STATUS);
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

        baseAreaDO.setStatus(VALID_STATUS);
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

        // 获取所有行政区
        List<BaseAreaDO> allArea = baseAreaDOMapper.getAll(VALID_STATUS);
        if (CollectionUtils.isEmpty(allArea)) {
            return ResultBean.ofSuccess(null);
        }

        // 省-市映射容器
        ConcurrentMap<Long, AreaVO> provCityMap = Maps.newConcurrentMap();

        // 省（全国）
        fillProv(allArea, provCityMap);
        // 市
        fillCity(allArea, provCityMap);

        // 中文排序,并返回结果
        List<AreaVO> areaVOList = sortAndGet(provCityMap);

        return ResultBean.ofSuccess(areaVOList);
    }

    /**
     * 解析并填充省
     *
     * @param allArea
     * @param provCityMap
     */
    private void fillProv(List<BaseAreaDO> allArea, ConcurrentMap<Long, AreaVO> provCityMap) {
        allArea.parallelStream()
                .filter(e -> null != e && null != e.getAreaId() && LEVEL_COUNTRY.equals(e.getLevel()) || LEVEL_PROV.equals(e.getLevel()))
                .forEach(e -> {

                    if (!provCityMap.containsKey(e.getAreaId())) {

                        AreaVO areaVO = new AreaVO();
                        areaVO.setId(e.getAreaId());
                        areaVO.setName(e.getAreaName());
                        areaVO.setLevel(e.getLevel());
                        areaVO.setCityList(Lists.newArrayList());

                        provCityMap.put(e.getAreaId(), areaVO);
                    }

                });
    }

    /**
     * 解析并补充市
     *
     * @param allArea
     * @param provCityMap
     */
    private void fillCity(List<BaseAreaDO> allArea, ConcurrentMap<Long, AreaVO> provCityMap) {
        allArea.stream()
                .filter(e -> null != e && null != e.getAreaId() && LEVEL_CITY.equals(e.getLevel()))
                .forEach(e -> {

                    if (provCityMap.containsKey(e.getParentAreaId())) {

                        AreaVO.City city = new AreaVO.City();
                        city.setId(e.getAreaId());
                        city.setName(e.getAreaName());
                        city.setLevel(e.getLevel());

                        provCityMap.get(e.getParentAreaId()).getCityList().add(city);
                    }

                });
    }

    /**
     * 排序并返回结果
     *
     * @param provCityMap
     * @return
     */
    private List<AreaVO> sortAndGet(ConcurrentMap<Long, AreaVO> provCityMap) {
        // 根据中文拼音排序
        Comparator<Object> chinaComparator = Collator.getInstance(java.util.Locale.CHINA);

        List<AreaVO> tmpAreaVOList = Lists.newArrayList();
        List<AreaVO> areaVOList = Lists.newArrayList();

        provCityMap.values().stream()
                .sorted(Comparator.comparing(AreaVO::getName, chinaComparator))
                .forEach(e -> {
                    // 全国拿出来放到第一位
                    if (COUNTRY.equals(e.getName())) {
                        areaVOList.add(e);
                    } else {
                        tmpAreaVOList.add(e);
                    }
                });
        areaVOList.addAll(tmpAreaVOList);

        return areaVOList;
    }


}
