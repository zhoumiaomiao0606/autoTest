package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.dao.mapper.CarBrandDOMapper;
import com.yunche.loan.dao.mapper.CarModelDOMapper;
import com.yunche.loan.domain.dataObj.CarBrandDO;
import com.yunche.loan.domain.dataObj.CarModelDO;
import com.yunche.loan.domain.viewObj.CarCascadeVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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

    private static final String CAR_CASCADE_CACHE_KEY = "CAR_CASCADE_CACHE";

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CarBrandDOMapper carBrandDOMapper;
    @Autowired
    private CarModelDOMapper carModelDOMapper;


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
}
