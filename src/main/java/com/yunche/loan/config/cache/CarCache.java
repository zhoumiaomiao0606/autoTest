package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yunche.loan.domain.entity.CarBrandDO;
import com.yunche.loan.domain.entity.CarDetailDO;
import com.yunche.loan.domain.entity.CarModelDO;
import com.yunche.loan.domain.vo.CarCascadeVO;
import com.yunche.loan.mapper.CarBrandDOMapper;
import com.yunche.loan.mapper.CarDetailDOMapper;
import com.yunche.loan.mapper.CarModelDOMapper;
import com.yunche.loan.service.CarService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
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

    private static final Logger logger = LoggerFactory.getLogger(CarCache.class);

    private static final String CAR_CASCADE_CACHE_KEY = "cascade:cache:car";

    private static final String CAR_BRAND_ALL_CACHE_KEY = "all:cache:car:brand";

    private static final String CAR_MODEL_ALL_CACHE_KEY = "all:cache:car:model";

    private static final String CAR_DETAIL_ALL_CACHE_KEY = "all:cache:car:detail";


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CarService carService;

    @Autowired
    private CarBrandDOMapper carBrandDOMapper;

    @Autowired
    private CarModelDOMapper carModelDOMapper;

    @Autowired
    private CarDetailDOMapper carDetailDOMapper;


    /**
     * 每周一4:00 更新车型库
     */
    @Scheduled(cron = "0 0 4 ? * MON")
    public void importCar() {
        logger.info("importCar start   >>>>>");
//        ResultBean<Void> resultBean = carService.importCar();
//        logger.info("importCar result >>>>> {}", JSON.toJSONString(resultBean));
        logger.info("importCar end   >>>>>");
    }

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

    @PostConstruct
    public void refresh() {
        // Cascade
        refreshCarCascade();

//        // 品牌
//        refreshAllCarBrand();
//        // 车系
//        refreshAllCarModel();
//        // 车型
//        refreshAllCarDetail();
    }

    private void refreshCarCascade() {
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

        carBrandList.stream()
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
     * <p>
     * 数据量太大 还不如用sql查询快
     *
     * @param carDetailId
     * @return
     */
    @Deprecated
    private CarDetailDO getCarDetail(Long carDetailId) {
        if (null == carDetailId) {
            return null;
        }

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_DETAIL_ALL_CACHE_KEY);
        String allCarDetail = boundValueOps.get();

        if (StringUtils.isBlank(allCarDetail)) {
            refreshAllCarDetail();
        }

        if (StringUtils.isNotBlank(allCarDetail)) {
            Map<String, JSONObject> map = JSON.parseObject(allCarDetail, Map.class);
            JSONObject jsonObject = map.get(String.valueOf(carDetailId));
            CarDetailDO carDetailDO = JSON.toJavaObject(jsonObject, CarDetailDO.class);
            return carDetailDO;
        }

        return null;
    }

    /**
     * 数据量太大 还不如用sql查询快
     *
     * @param carModelId
     * @return
     */
    @Deprecated
    private CarModelDO getCarModel(Long carModelId) {
        if (null == carModelId) {
            return null;
        }

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_MODEL_ALL_CACHE_KEY);
        String allCarModel = boundValueOps.get();

        if (StringUtils.isBlank(allCarModel)) {
            refreshAllCarModel();
        }

        if (StringUtils.isNotBlank(allCarModel)) {
            Map<String, JSONObject> map = JSON.parseObject(allCarModel, Map.class);
            JSONObject jsonObject = map.get(String.valueOf(carModelId));
            CarModelDO carModelDO = JSON.toJavaObject(jsonObject, CarModelDO.class);
            return carModelDO;
        }

        return null;
    }

    /**
     * 数据量太大 还不如用sql查询快
     *
     * @param carBrandId
     * @return
     */
    @Deprecated
    private CarBrandDO getCarBrand(Long carBrandId) {
        if (null == carBrandId) {
            return null;
        }

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_BRAND_ALL_CACHE_KEY);
        String allCarBrand = boundValueOps.get();

        if (StringUtils.isBlank(allCarBrand)) {
            refreshAllCarBrand();
        }

        if (StringUtils.isNotBlank(allCarBrand)) {
            Map<String, JSONObject> map = JSON.parseObject(allCarBrand, Map.class);
            JSONObject jsonObject = map.get(String.valueOf(carBrandId));
            CarBrandDO carBrandDO = JSON.toJavaObject(jsonObject, CarBrandDO.class);
            return carBrandDO;
        }

        return null;
    }

    private void refreshAllCarBrand() {

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

    private void refreshAllCarModel() {

        Map<String, CarModelDO> idCarModelMap = Maps.newConcurrentMap();

        List<CarModelDO> allCarModel = carModelDOMapper.getAll(VALID_STATUS);
        if (!CollectionUtils.isEmpty(allCarModel)) {
            allCarModel.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        idCarModelMap.put(String.valueOf(e.getId()), e);
                    });
        }

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(CAR_MODEL_ALL_CACHE_KEY);
        boundValueOps.set(JSON.toJSONString(idCarModelMap));
    }

    private void refreshAllCarDetail() {

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
