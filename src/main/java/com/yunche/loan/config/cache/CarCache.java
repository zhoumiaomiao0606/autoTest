package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yunche.loan.domain.entity.CarDetailDO;
import com.yunche.loan.mapper.CarBrandDOMapper;
import com.yunche.loan.mapper.CarDetailDOMapper;
import com.yunche.loan.mapper.CarModelDOMapper;
import com.yunche.loan.domain.entity.CarBrandDO;
import com.yunche.loan.domain.entity.CarModelDO;
import com.yunche.loan.domain.vo.CarCascadeVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * 车型库缓存
 *
 * @author liuzhe
 * @date 2018/2/2
 */
@Component
public class CarCache {

    private static final String CAR_CASCADE_CACHE_KEY = "cascade:cache:car";

    private static final String CAR_BRAND_ALL_CACHE_KEY = "all:cache:car:brand";

    private static final String CAR_MODEL_ALL_CACHE_KEY = "all:cache:car:model";

    private static final String CAR_DETAIL_ALL_CACHE_KEY = "all:cache:car:detail";


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CarBrandDOMapper carBrandDOMapper;

    @Autowired
    private CarModelDOMapper carModelDOMapper;

    @Autowired
    private CarDetailDOMapper carDetailDOMapper;


    public CarCascadeVO get() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_CASCADE_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, CarCascadeVO.class);
        }

        // 刷新缓存
        refresh();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, CarCascadeVO.class);
        }
        return null;
    }

    //    @PostConstruct
    private void refresh() {
        CarCascadeVO carCascadeVO = new CarCascadeVO();

        // 获取并填充所有品牌
        getAndFillAllCarBrand(carCascadeVO);

        // 获取并填充所有子车系
        getAndFillAllCarModel(carCascadeVO);

        // 刷新缓存
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_CASCADE_CACHE_KEY);
        boundValueOps.set(JSON.toJSONString(carCascadeVO));
    }

    /**
     * 获取并填充所有汽车品牌对象  -All  两级联动
     *
     * @param carCascadeVO
     */
    private void getAndFillAllCarBrand(CarCascadeVO carCascadeVO) {
        // getAll
        List<CarBrandDO> carBrandDOS = carBrandDOMapper.getAll(VALID_STATUS);

        if (!CollectionUtils.isEmpty(carBrandDOS)) {
            List<CarCascadeVO.Brand> carBrandList = carBrandDOS.stream()
                    .filter(e -> null != e && null != e.getId())
                    .map(e -> {

                        CarCascadeVO.Brand brand = new CarCascadeVO.Brand();
                        BeanUtils.copyProperties(e, brand);

                        return brand;
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(CarCascadeVO.Brand::getInitial))
                    .collect(Collectors.toList());

            carCascadeVO.setCarBrand(carBrandList);
        }

    }

    /**
     * 获取并填充所有子车系及子车型   -两级联动
     *
     * @param carCascadeVO
     */
    private void getAndFillAllCarModel(CarCascadeVO carCascadeVO) {
        List<CarCascadeVO.Brand> carBrandList = carCascadeVO.getCarBrand();
        if (CollectionUtils.isEmpty(carBrandList)) {
            return;
        }

        carBrandList.parallelStream()
                .filter(b -> null != b && null != b.getId())
                .forEach(b -> {

                    // 根据品牌ID获取所有子车系
                    List<CarCascadeVO.Model> carModelList = getAllCarModel(b.getId());
                    // fill
                    b.setCarModel(carModelList);

                });
    }

    /**
     * 根据品牌ID获取所有子车系
     *
     * @param brandId
     * @return
     */
    private List<CarCascadeVO.Model> getAllCarModel(Long brandId) {
        // 获取所有子车系
        List<CarModelDO> carModelDOS = carModelDOMapper.getModelListByBrandId(brandId, VALID_STATUS);
        if (CollectionUtils.isEmpty(carModelDOS)) {
            return Collections.EMPTY_LIST;
        }

        // 填充所有子车系
        List<CarCascadeVO.Model> carModelList = carModelDOS.stream()
                .filter(m -> null != m && null != m.getBrandId())
                .map(m -> {

                    CarCascadeVO.Model model = new CarCascadeVO.Model();
                    BeanUtils.copyProperties(m, model);

                    return model;
                })
                .collect(Collectors.toList());

        return carModelList;
    }

    /**
     * 获取
     *
     * @param carDetailId
     * @return
     */
    public CarDetailDO getCarDetail(Long carDetailId) {
        if (null == carDetailId) {
            return null;
        }

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_DETAIL_ALL_CACHE_KEY);
        String allCarDetail = boundValueOps.get();

        if (StringUtils.isBlank(allCarDetail)) {
            cacheAllCarDetail();
        }

        if (StringUtils.isNotBlank(allCarDetail)) {
            Map<String, CarDetailDO> map = JSON.parseObject(allCarDetail, Map.class);
            CarDetailDO carDetailDO = map.get(String.valueOf(carDetailId));
            return carDetailDO;
        }

        return null;
    }

    /**
     * @param carModelId
     * @return
     */
    public CarModelDO getCarModel(Long carModelId) {
        if (null == carModelId) {
            return null;
        }

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_MODEL_ALL_CACHE_KEY);
        String allCarModel = boundValueOps.get();

        if (StringUtils.isBlank(allCarModel)) {
            cacheAllCarModel();
        }

        if (StringUtils.isNotBlank(allCarModel)) {
            Map<String, CarModelDO> map = JSON.parseObject(allCarModel, Map.class);
            CarModelDO carModelDO = map.get(String.valueOf(carModelId));
            return carModelDO;
        }

        return null;
    }

    /**
     * @param carBrandId
     * @return
     */
    public CarBrandDO getCarBrand(Long carBrandId) {
        if (null == carBrandId) {
            return null;
        }

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_BRAND_ALL_CACHE_KEY);
        String allCarBrand = boundValueOps.get();

        if (StringUtils.isBlank(allCarBrand)) {
            cacheAllCarBrand();
        }

        if (StringUtils.isNotBlank(allCarBrand)) {
            Map<String, CarBrandDO> map = JSON.parseObject(allCarBrand, Map.class);
            CarBrandDO carBrandDO = map.get(String.valueOf(carBrandId));
            return carBrandDO;
        }

        return null;
    }

//    @PostConstruct
    public void cacheAllCarBrand() {

        Map<String, CarBrandDO> idCarBrandMap = Maps.newConcurrentMap();

        List<CarBrandDO> allCarBrand = carBrandDOMapper.getAll(VALID_STATUS);
        if (!CollectionUtils.isEmpty(allCarBrand)) {
            allCarBrand.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        idCarBrandMap.put(String.valueOf(e.getId()), e);
                    });
        }

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_BRAND_ALL_CACHE_KEY);
        boundValueOps.set(JSON.toJSONString(idCarBrandMap));
    }

//    @PostConstruct
    public void cacheAllCarModel() {

        Map<String, CarModelDO> idCarModelMap = Maps.newConcurrentMap();

        List<CarModelDO> allCarModel = carModelDOMapper.getAll(VALID_STATUS);
        if (!CollectionUtils.isEmpty(allCarModel)) {
            allCarModel.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        idCarModelMap.put(String.valueOf(e.getId()), e);
                    });
        }

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_MODEL_ALL_CACHE_KEY);
        boundValueOps.set(JSON.toJSONString(idCarModelMap));
    }

//    @PostConstruct
    public void cacheAllCarDetail() {

        Map<String, CarDetailDO> idCarDetailMap = Maps.newConcurrentMap();

        List<CarDetailDO> allCarDetail = carDetailDOMapper.getAll(VALID_STATUS);
        if (!CollectionUtils.isEmpty(allCarDetail)) {
            allCarDetail.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        idCarDetailMap.put(String.valueOf(e.getId()), e);
                    });
        }

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_DETAIL_ALL_CACHE_KEY);
        boundValueOps.set(JSON.toJSONString(idCarDetailMap));
    }
}
