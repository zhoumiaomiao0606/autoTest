package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.dao.BaseAreaDOMapper;
import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.vo.CascadeAreaVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static com.yunche.loan.config.constant.AreaConst.*;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static java.util.Locale.CHINA;

/**
 * 省市-城市缓存
 *
 * @author liuzhe
 * @date 2018/2/2
 */
@Component
public class AreaCache {

    /**
     * 级联缓存KEY
     */
    private static final String CASCADE_CACHE_AREA_KEY = "cascade:cache:area";
    /**
     * 所有AREA缓存KEY
     */
    private static final String ALL_CACHE_AREA_KEY = "all:cache:area";

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;


    /**
     * 获取CASCADE_AREA缓存
     *
     * @return
     */
    public List<CascadeAreaVO> get() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CASCADE_CACHE_AREA_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, CascadeAreaVO.class);
        }

        // 刷新缓存
        refresh();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, CascadeAreaVO.class);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 刷新CASCADE_AREA缓存
     */
//    @PostConstruct
    public void refresh() {
        // 获取所有行政区
        List<BaseAreaDO> allArea = baseAreaDOMapper.getAll(VALID_STATUS);
        if (CollectionUtils.isEmpty(allArea)) {
            return;
        }

        // 附带刷新所有
        refreshAll(allArea);

        // 省-市映射容器
        ConcurrentMap<Long, CascadeAreaVO> provCityMap = Maps.newConcurrentMap();

        // 省（全国）
        fillProv(allArea, provCityMap);
        // 市
        fillCity(allArea, provCityMap);

        // 中文排序,并返回结果
        List<CascadeAreaVO> cascadeAreaVOList = sortAndGet(provCityMap);

        // 刷新缓存
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CASCADE_CACHE_AREA_KEY);
        boundValueOps.set(JSON.toJSONString(cascadeAreaVOList));
    }

    /**
     * 获取ALL_AREA缓存
     *
     * @return
     */
    public List<BaseAreaDO> getAll() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(ALL_CACHE_AREA_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, BaseAreaDO.class);
        }

        // 刷新缓存
        refreshAll();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, BaseAreaDO.class);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 刷新ALL_AREA缓存
     */
//    @PostConstruct
    public void refreshAll() {
        // 获取所有行政区
        List<BaseAreaDO> allArea = baseAreaDOMapper.getAll(VALID_STATUS);
        if (CollectionUtils.isEmpty(allArea)) {
            return;
        }

        // 执行刷新
        refreshAll(allArea);
    }

    /**
     * 刷新ALL_AREA缓存
     *
     * @param allArea
     */
    public void refreshAll(List<BaseAreaDO> allArea) {
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(ALL_CACHE_AREA_KEY);
        boundValueOps.set(JSON.toJSONString(allArea));
    }


    /**
     * 获取所有父级id
     *
     * @param childAreaId
     * @return
     */
    public List<Long> getAllSuperAreaIdList(Long childAreaId) {
        if (null == childAreaId) {
            return null;
        }

        // getAll
        List<BaseAreaDO> allArea = getAll();

        // 获取所有父级ID
        List<Long> allSuperAreaIdList = getAllSuperAreaIdList(childAreaId, allArea);

        return allSuperAreaIdList;
    }

    /**
     * 执行递归遍历，获取所有父级id
     *
     * @param childAreaId
     * @param allArea
     * @return
     */
    private List<Long> getAllSuperAreaIdList(Long childAreaId, List<BaseAreaDO> allArea) {
        List<Long> allSuperAreaIdList = Lists.newArrayList();
        allArea.parallelStream()
                .filter(e -> null != e && null != e.getAreaId() && childAreaId.equals(e.getAreaId()) && null != e.getParentAreaId())
                .forEach(e -> {

                    Long areaId = e.getParentAreaId();
                    // 递归调用
                    List<Long> superAreaIdList = getAllSuperAreaIdList(areaId, allArea);
                    superAreaIdList.add(areaId);

                    allSuperAreaIdList.addAll(superAreaIdList);
                });

        return allSuperAreaIdList;
    }

    /**
     * 获取所有子级id
     *
     * @param parentAreaId
     * @return
     */
    public List<Long> getAllChildAreaIdList(Long parentAreaId) {
        if (null == parentAreaId) {
            return null;
        }

        // getAll
        List<BaseAreaDO> allArea = getAll();

        // 获取所有子级ID
        List<Long> allChildAreaIdList = getAllChildAreaIdList(parentAreaId, allArea);

        return allChildAreaIdList;
    }

    /**
     * 执行递归遍历，获取所有子级id
     *
     * @param parentAreaId
     * @param allArea
     * @return
     */
    private List<Long> getAllChildAreaIdList(Long parentAreaId, List<BaseAreaDO> allArea) {

        List<Long> allChildAreaIdList = Lists.newArrayList();
        allArea.parallelStream()
                .filter(e -> null != e && null != e.getAreaId() && parentAreaId.equals(e.getParentAreaId()))
                .forEach(e -> {

                    Long areaId = e.getAreaId();
                    // 递归调用
                    List<Long> childAreaIdList = getAllChildAreaIdList(areaId, allArea);
                    childAreaIdList.add(areaId);

                    allChildAreaIdList.addAll(childAreaIdList);
                });

        return allChildAreaIdList;
    }


    /**
     * 解析并填充省
     *
     * @param allArea
     * @param provCityMap
     */
    private void fillProv(List<BaseAreaDO> allArea, ConcurrentMap<Long, CascadeAreaVO> provCityMap) {
        allArea.parallelStream()
                .filter(e -> null != e && null != e.getAreaId() && LEVEL_COUNTRY.equals(e.getLevel()) || LEVEL_PROV.equals(e.getLevel()))
                .forEach(e -> {

                    if (!provCityMap.containsKey(e.getAreaId())) {

                        CascadeAreaVO cascadeAreaVO = new CascadeAreaVO();
                        cascadeAreaVO.setId(e.getAreaId());
                        cascadeAreaVO.setName(e.getAreaName());
                        cascadeAreaVO.setLevel(e.getLevel());
                        cascadeAreaVO.setCityList(Lists.newArrayList());

                        provCityMap.put(e.getAreaId(), cascadeAreaVO);
                    }

                });
    }

    /**
     * 解析并补充市
     *
     * @param allArea
     * @param provCityMap
     */
    private void fillCity(List<BaseAreaDO> allArea, ConcurrentMap<Long, CascadeAreaVO> provCityMap) {
        allArea.parallelStream()
                .filter(e -> null != e && null != e.getAreaId() && LEVEL_CITY.equals(e.getLevel()))
                .forEach(e -> {

                    if (provCityMap.containsKey(e.getParentAreaId())) {

                        CascadeAreaVO.City city = new CascadeAreaVO.City();
                        city.setId(e.getAreaId());
                        city.setName(e.getAreaName());
                        city.setLevel(e.getLevel());

                        provCityMap.get(e.getParentAreaId()).getCityList().add(city);
                    }

                });
    }

    /**
     * 按照首字母排序并返回结果
     *
     * @param provCityMap
     * @return
     */
    private List<CascadeAreaVO> sortAndGet(ConcurrentMap<Long, CascadeAreaVO> provCityMap) {
        // 根据中文拼音排序
        Comparator<Object> chinaComparator = Collator.getInstance(CHINA);

        List<CascadeAreaVO> tmpCascadeAreaVOList = Lists.newArrayList();
        List<CascadeAreaVO> cascadeAreaVOList = Lists.newArrayList();

        provCityMap.values().stream()
                .sorted(Comparator.comparing(CascadeAreaVO::getName, chinaComparator))
                .forEach(e -> {
                    // 全国拿出来放到第一位
                    if (COUNTRY.equals(e.getName())) {
                        cascadeAreaVOList.add(e);
                    } else {
                        tmpCascadeAreaVOList.add(e);
                    }
                });
        cascadeAreaVOList.addAll(tmpCascadeAreaVOList);

        return cascadeAreaVOList;
    }
}
