package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.dao.mapper.BaseAreaDOMapper;
import com.yunche.loan.domain.dataObj.BaseAreaDO;
import com.yunche.loan.domain.viewObj.CascadeAreaVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
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

    private static final String AREA_CASCADE_CACHE_KEY = "AREA_CASCADE_CACHE";

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;


    public List<CascadeAreaVO> get() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(AREA_CASCADE_CACHE_KEY);
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

    @PostConstruct
    public void refresh() {
        // 获取所有行政区
        List<BaseAreaDO> allArea = baseAreaDOMapper.getAll(VALID_STATUS);
        if (CollectionUtils.isEmpty(allArea)) {
            return;
        }

        // 省-市映射容器
        ConcurrentMap<Long, CascadeAreaVO> provCityMap = Maps.newConcurrentMap();

        // 省（全国）
        fillProv(allArea, provCityMap);
        // 市
        fillCity(allArea, provCityMap);

        // 中文排序,并返回结果
        List<CascadeAreaVO> cascadeAreaVOList = sortAndGet(provCityMap);

        // 刷新缓存
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(AREA_CASCADE_CACHE_KEY);
        boundValueOps.set(JSON.toJSONString(cascadeAreaVOList));
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
        allArea.stream()
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
