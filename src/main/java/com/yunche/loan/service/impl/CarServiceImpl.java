package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yunche.loan.config.cache.CarCache;
import com.yunche.loan.config.util.HttpUtils;
import com.yunche.loan.mapper.CarBrandDOMapper;
import com.yunche.loan.mapper.CarDetailDOMapper;
import com.yunche.loan.mapper.CarModelDOMapper;
import com.yunche.loan.domain.entity.CarBrandDO;
import com.yunche.loan.domain.entity.CarDetailDO;
import com.yunche.loan.domain.entity.CarModelDO;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.CarThreeLevelVO;
import com.yunche.loan.domain.vo.CarCascadeVO;
import com.yunche.loan.service.CarService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.CarConst.*;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Service
public class CarServiceImpl implements CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarServiceImpl.class);
    /**
     * 换行符
     */
    public static final String NEW_LINE = System.getProperty("line.separator");


    /**
     * 阿里云车型大全API服务——HOST
     * tips：接口限流 30次/min     ===> 大概估算一下：跑一次大概近15个小时
     */
    private static final String host = "http://jisucxdq.market.alicloudapi.com";
    /**
     * 阿里云车型大全API服务——appcode
     */
    private static final String appcode = "567ef51853094159a974a2955f312590";

    @Autowired
    private CarBrandDOMapper carBrandDOMapper;

    @Autowired
    private CarModelDOMapper carModelDOMapper;

    @Autowired
    private CarDetailDOMapper carDetailDOMapper;

    @Autowired
    private CarCache carCache;


    @Override
    public ResultBean<Void> importCar() {
        long startTime = System.currentTimeMillis();

        // 车系ID-车型ID列表映射  ——  k/v : modelId / detailIdList
        Map<Long, List<Long>> modelIdDetailIdsMap = Maps.newHashMap();

        // 获取品牌数据
        logger.info("查询品牌开始   >>>>>   ");
        List<CarBrandDO> carBrandDOS = getCarBrand();
        logger.info("查询品牌完成   >>>>>   ");

        logger.info("插入品牌数据开始   >>>>>   ");
        insertBrand(carBrandDOS);
        logger.info("插入品牌数据完成   >>>>>   ");


        // 获取车系数据
        logger.info("查询&插入车系开始   >>>>>   ");
        List<CarModelDO> needFillCarModelDOS = insertAndGetCarModel(carBrandDOS, modelIdDetailIdsMap);
        logger.info("查询&插入车系完成   >>>>>   ");

        // 获取车型数据，并补充车系数据(price、seatNum)
        logger.info("查询&插入车型开始   >>>>>   ");
        insertCarDetailAndFillCarModel(needFillCarModelDOS, modelIdDetailIdsMap);
        logger.info("查询&插入车型完成   >>>>>   ");

        long totalTime = System.currentTimeMillis() - startTime;
        logger.info("/car/import：导入车型库总耗时 : {}min{}s", (totalTime / 1000) / 60, (totalTime / 1000) % 60);

        // 刷新缓存
        carCache.refresh();

        return ResultBean.ofSuccess(null, "导入成功");
    }

    @Override
    public ResultBean<Void> fillModel() {
        long startTime = System.currentTimeMillis();

        // 获取所有 model_id —— detail_id
        List<CarDetailDO> carDetailDOS = carDetailDOMapper.getAllIdAndModelId(null);
        logger.info("car_detail表数据总量为 : " + carDetailDOS.size());
        if (CollectionUtils.isEmpty(carDetailDOS)) {
            return ResultBean.ofSuccess(null, "car_detail表为空表,无可更新数据.");
        }

        // 车系ID-车系实体 映射关系： model_id —— CarModellDO 映射
        ConcurrentMap<Long, CarModelDO> idModelDOMap = getIdModelDOMapping();

        // 车型ID-车型实体 映射关系： detail_id —— CarDetailDO 映射
        ConcurrentMap<Long, CarDetailDO> idDetailDOMap = getIdDetailDOMapping();

        // 梳理id映射关系： model_id —— detail_id列表
        ConcurrentMap<Long, List<Long>> modelIdDetailIdsMap = getModelIdDetailIdsMapping(carDetailDOS);

        // 执行补偿任务
        execFillModel(idModelDOMap, idDetailDOMap, modelIdDetailIdsMap);

        long totalTime = System.currentTimeMillis() - startTime;
        logger.info("/car/fillModel：车系表补偿任务总耗时 : {}min", new BigDecimal(totalTime).doubleValue() / 60000);

        return ResultBean.ofSuccess(null, "补偿任务执行完成");
    }

    @Override
    public ResultBean<Map<String, Integer>> count() {
        // 获取品牌数据
        logger.info("查询品牌开始   >>>>>   ");
        List<CarBrandDO> carBrandDOS = getCarBrand();
        logger.info("查询品牌完成   >>>>>   ");

        logger.info("统计数量开始   >>>>>   ");
        Map<String, Integer> countMap = countTotal(carBrandDOS);
        logger.info("统计数量完成   >>>>>   ");

        return ResultBean.ofSuccess(countMap);
    }

    /**
     * 三级联动关系 -All
     *
     * @return
     */
    @Override
    public ResultBean<CarThreeLevelVO> listAll() {
        CarThreeLevelVO carThreeLevelVO = new CarThreeLevelVO();

        // 获取并填充所有品牌
        getAndFillAllCarBrand(carThreeLevelVO);

        // 获取并填充所有子车系及子车型
        getAndFillAllCarModelAndDetail(carThreeLevelVO);

        return ResultBean.ofSuccess(carThreeLevelVO);
    }

    /**
     * 三级联动关系  -One
     * <p>
     * 单个品牌下
     *
     * @param brandId
     * @return
     */
    @Override
    public ResultBean<CarThreeLevelVO.CarOneBrandThreeLevelVO> list(Long brandId) {
        CarThreeLevelVO.CarOneBrandThreeLevelVO carOneBrandThreeLevelVO = new CarThreeLevelVO.CarOneBrandThreeLevelVO();

        // 跟前品牌ID,获取并填充当前品牌
        getAndFillOneCarBrand(carOneBrandThreeLevelVO, brandId);

        // 获取并填充所有子车系及子车型
        getAndFillOneCarModelAndDetail(carOneBrandThreeLevelVO);

        return ResultBean.ofSuccess(carOneBrandThreeLevelVO);
    }

    /**
     * 品牌-车系  两级联动
     *
     * @return
     */
    @Override
    public ResultBean<CarCascadeVO> listTwoLevel() {
        // 走缓存
        CarCascadeVO carCascadeVO = carCache.get();
        return ResultBean.ofSuccess(carCascadeVO);
    }

    @Override
    public ResultBean<String> getFullName(Long carId, Byte carType) {
        Preconditions.checkNotNull(carId, "车辆ID不能为空");
        Preconditions.checkNotNull(carType, "车辆等级不能为空");

        String carName = "";

        if (CAR_DETAIL.equals(carType)) {
            CarDetailDO carDetailDO = carDetailDOMapper.selectByPrimaryKey(carId, null);
            if (null != carDetailDO && null != carDetailDO.getModelId()) {
                CarModelDO carModelDO = carModelDOMapper.selectByPrimaryKey(carDetailDO.getModelId(), null);
                if (null != carModelDO && null != carModelDO.getBrandId()) {
                    CarBrandDO carBrandDO = carBrandDOMapper.selectByPrimaryKey(carModelDO.getBrandId(), null);
                    if (null != carBrandDO) {
                        carName = carName + " " + carBrandDO.getName();
                    }
                    carName = carName + " " + carModelDO.getName();
                }
                carName = carName + " " + carDetailDO.getName();
            }
        } else if (CAR_MODEL.equals(carType)) {
            CarModelDO carModelDO = carModelDOMapper.selectByPrimaryKey(carId, null);
            if (null != carModelDO && null != carModelDO.getBrandId()) {
                CarBrandDO carBrandDO = carBrandDOMapper.selectByPrimaryKey(carModelDO.getBrandId(), null);
                if (null != carBrandDO) {
                    carName = carName + " " + carBrandDO.getName();
                }
                carName = carName + " " + carModelDO.getName();
            }
        } else if (CAR_BRAND.equals(carType)) {
            CarBrandDO carBrandDO = carBrandDOMapper.selectByPrimaryKey(carId, null);
            if (null != carBrandDO) {
                carName = carName + " " + carBrandDO.getName();
            }
        }

        return ResultBean.ofSuccess(carName.trim());
    }

    /**
     * 跟前品牌ID,获取并填充当前品牌   -ONE
     *
     * @param carOneBrandThreeLevelVO
     * @param brandId
     */
    private void getAndFillOneCarBrand(CarThreeLevelVO.CarOneBrandThreeLevelVO carOneBrandThreeLevelVO, Long brandId) {
        // getAll
        CarBrandDO carBrandDO = carBrandDOMapper.selectByPrimaryKey(brandId, VALID_STATUS);
        // fill
        if (null != carBrandDO) {
            CarThreeLevelVO.Brand brand = new CarThreeLevelVO.Brand();
            BeanUtils.copyProperties(carBrandDO, brand);
            carOneBrandThreeLevelVO.setCarBrand(brand);
        }
    }


    /**
     * 获取并填充所有汽车品牌对象  -All
     *
     * @param carThreeLevelVO
     * @return
     */
    public void getAndFillAllCarBrand(CarThreeLevelVO carThreeLevelVO) {
        // getAll
        List<CarBrandDO> carBrandDOS = carBrandDOMapper.getAll(VALID_STATUS);

        if (!CollectionUtils.isEmpty(carBrandDOS)) {
            List<CarThreeLevelVO.Brand> carBrandList = carBrandDOS.stream()
                    .filter(e -> null != e && null != e.getId())
                    .map(e -> {

                        CarThreeLevelVO.Brand brand = new CarThreeLevelVO.Brand();
                        BeanUtils.copyProperties(e, brand);

                        return brand;
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(CarThreeLevelVO.Brand::getInitial))
                    .collect(Collectors.toList());

            carThreeLevelVO.setCarBrand(carBrandList);
        }
    }

    /**
     * 获取品牌的所有子车系 -One
     *
     * @param carOneBrandThreeLevelVO
     */
    private void getAndFillOneCarModelAndDetail(CarThreeLevelVO.CarOneBrandThreeLevelVO carOneBrandThreeLevelVO) {
        CarThreeLevelVO.Brand brand = carOneBrandThreeLevelVO.getCarBrand();
        if (null == brand) {
            return;
        }

        // getModel
        List<CarThreeLevelVO.Model> carModelList = getAllCarModelAndDetail(brand.getId());

        brand.setCarModel(carModelList);
    }

    /**
     * 获取所有子车系（及车系下的车型，并填充车型到车车系下）
     *
     * @param brandId
     * @return
     */
    private List<CarThreeLevelVO.Model> getAllCarModelAndDetail(Long brandId) {
        // 获取所有子车系
        List<CarModelDO> carModelDOS = carModelDOMapper.getModelListByBrandId(brandId, VALID_STATUS);
        if (CollectionUtils.isEmpty(carModelDOS)) {
            return Collections.EMPTY_LIST;
        }

        // 填充所有子车系
        List<CarThreeLevelVO.Model> carModelList = carModelDOS.stream()
                .filter(m -> null != m && null != m.getBrandId())
                .map(m -> {

                    CarThreeLevelVO.Model model = new CarThreeLevelVO.Model();
                    BeanUtils.copyProperties(m, model);

                    // 获取所有子车型
                    List<CarThreeLevelVO.Detail> carDetailList = getCarDetailList(m.getId());
                    // 填充所有子车型
                    model.setCarDetail(carDetailList);

                    return model;
                })
                .collect(Collectors.toList());

        return carModelList;
    }


    /**
     * 获取品牌的所有子车系 -All
     *
     * @param carThreeLevelVO
     */
    private void getAndFillAllCarModelAndDetail(CarThreeLevelVO carThreeLevelVO) {
        List<CarThreeLevelVO.Brand> carBrandList = carThreeLevelVO.getCarBrand();
        if (CollectionUtils.isEmpty(carBrandList)) {
            return;
        }

        carBrandList.parallelStream()
                .filter(b -> null != b && null != b.getId())
                .forEach(b -> {

                    // getModel
                    List<CarThreeLevelVO.Model> carModelList = getAllCarModelAndDetail(b.getId());
                    b.setCarModel(carModelList);

                });

    }

    /**
     * 根据车系ID获取所有子车型
     *
     * @param modelId
     * @return
     */
    private List<CarThreeLevelVO.Detail> getCarDetailList(Long modelId) {
        List<CarDetailDO> carDetailDOS = carDetailDOMapper.getDetailListByModelId(modelId, null);
        if (!CollectionUtils.isEmpty(carDetailDOS)) {
            List<CarThreeLevelVO.Detail> carDetailList = carDetailDOS.stream()
                    .filter(d -> null != d && null != d.getId())
                    .map(d -> {

                        CarThreeLevelVO.Detail detail = new CarThreeLevelVO.Detail();
                        BeanUtils.copyProperties(d, detail);

                        return detail;

                    })
                    .collect(Collectors.toList());
            return carDetailList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 统计数目
     *
     * @param carBrandDOS
     */
    private Map<String, Integer> countTotal(List<CarBrandDO> carBrandDOS) {

        Map<String, Integer> countMap = Maps.newHashMap();
        countMap.put("car_brand_count", carBrandDOS.size());
        countMap.put("car_model_count", 0);
        countMap.put("car_detail_count", 0);

        String path = "/car/carlist";
        String method = "GET";

        // headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode);

        // query
        Map<String, String> querys = new HashMap<>();

        carBrandDOS.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    querys.put("parentid", e.getId().toString());

                    requestAndParseThenCount(host, path, method, headers, querys, countMap);

                    wait_();
                });

        return countMap;
    }

    /**
     * 请求/car/carlist接口，解析数据，然后统计数量
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param countMap
     */
    private void requestAndParseThenCount(String host, String path, String method, Map<String, String> headers, Map<String, String> querys, Map<String, Integer> countMap) {

        try {

            JSONObject bodyObj = requestAndCheckThenReturnResult(host, path, method, headers, querys);
            if (CollectionUtils.isEmpty(bodyObj)) {
                return;
            }

            JSONArray resultJsonArr = bodyObj.getJSONArray("result");
            if (CollectionUtils.isEmpty(resultJsonArr)) {
                return;
            }

            resultJsonArr.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        // 子公司   depth = 2
                        JSONObject eObj = (JSONObject) e;

                        // 车系列表    carlist : depth = 3
                        JSONArray modelList = eObj.getJSONArray("carlist");
                        if (!CollectionUtils.isEmpty(modelList)) {
                            countMap.put("car_model_count", countMap.get("car_model_count") + modelList.size());

                            modelList.stream()
                                    .filter(Objects::nonNull)
                                    .forEach(m -> {
                                        JSONObject mObj = (JSONObject) m;
                                        // 车系列表     list : depth = 4
                                        JSONArray detailList = mObj.getJSONArray("list");
                                        if (!CollectionUtils.isEmpty(detailList)) {
                                            countMap.put("car_detail_count", countMap.get("car_detail_count") + detailList.size());
                                        }
                                    });
                        }


                    });

        } catch (Exception e) {
            logger.error(path + ": 接口调用失败", e);
        }
    }

    /**
     * 执行车系（car_model表）的price、seatNum补偿任务
     *
     * @param idModelDOMap
     * @param idDetailDOMap
     * @param modelIdDetailIdsMap
     */
    private void execFillModel(Map<Long, CarModelDO> idModelDOMap, Map<Long, CarDetailDO> idDetailDOMap, Map<Long, List<Long>> modelIdDetailIdsMap) {
        // 容器
        ArrayList<Double> minMaxPrice = Lists.newArrayList();
        Set<Integer> seatNumSet = Sets.newTreeSet();

        modelIdDetailIdsMap.forEach((modelId, detailIds) -> {

            detailIds.stream()
                    .forEach(detailId -> {

                        CarDetailDO carDetailDO = idDetailDOMap.get(detailId);
                        if (null != carDetailDO) {
                            // 统计price
                            statisticsPrice(carDetailDO.getPrice(), minMaxPrice);
                            // 统计seatNum
                            statisticsSeatNum(carDetailDO.getSeatNum(), seatNumSet);
                        }

                    });

            // 获取当前要补偿数据的CarModelDO
            CarModelDO carModelDO = idModelDOMap.get(modelId);
            if (null != carModelDO) {
                // 补充price
                fillCarModelPrice(carModelDO, minMaxPrice);
                // 补充seatNum
                fillCarModelSeatNum(carModelDO, seatNumSet);
            }
            // 清空容器
            minMaxPrice.clear();
            seatNumSet.clear();

        });
    }

    private void insertBrand(List<CarBrandDO> carBrandDOS) {

        int totalNum = carBrandDOS.size();

        // 获取已存在
        List<Integer> existBrandIdList = carBrandDOMapper.getAllId(null);
        int existNum = CollectionUtils.isEmpty(existBrandIdList) ? 0 : existBrandIdList.size();
        logger.info("获取已存在的品牌ID列表   >>>>>   已存在的品牌总量：" + existNum);

        List<CarBrandDO> notExistCarBrandDOS = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(existBrandIdList)) {
            carBrandDOS.stream()
                    .filter(e -> null != e && null != e.getId())
                    .forEach(e -> {
                        // 不存在，则插入
                        if (!existBrandIdList.contains(e.getId())) {
                            notExistCarBrandDOS.add(e);
                        }
                    });
        }
        logger.info("剔除已存在的品牌   >>>>>   总量 : {}, 已存在 : {},  剔除 : {}, 剩余 : {}.",
                totalNum, existNum, totalNum - notExistCarBrandDOS.size(), notExistCarBrandDOS.size());

        logger.info("插入剩余品牌开始   >>>>>   ");
        if (!CollectionUtils.isEmpty(notExistCarBrandDOS)) {
            Integer count = carBrandDOMapper.batchInsert(notExistCarBrandDOS);
            logger.info("插入剩余品牌结束   >>>>>   成功新插入品牌数量 : {}", count);
        }
    }

    /**
     * 获取车型数据，并补充车系数据(price、seatNum)
     *
     * @param needFillCarModelDOS
     * @param modelIdDetailIdsMap
     * @return
     */
    private void insertCarDetailAndFillCarModel(List<CarModelDO> needFillCarModelDOS, Map<Long, List<Long>> modelIdDetailIdsMap) {
        if (CollectionUtils.isEmpty(modelIdDetailIdsMap)) {
            return;
        }

        // exist
        List<Long> existCarDetailIds = carDetailDOMapper.getAllId(null);

        // 极值容器
        List<Double> minMaxPrice = Lists.newArrayList();
        Set<Integer> seatNumSet = Sets.newHashSet();

        needFillCarModelDOS.stream()
                .filter(m -> null != m && null != m.getId())
                .forEach(m -> {

                    Long modelId = m.getId();
                    List<Long> detailIds = modelIdDetailIdsMap.get(modelId);
                    if (!CollectionUtils.isEmpty(detailIds)) {

                        detailIds.stream()
                                .forEach(detailId -> {

                                    // 不存在，则插入
                                    if (!existCarDetailIds.contains(detailId)) {
                                        // 获取车型数据
                                        CarDetailDO carDetailDO = getCarDetail(modelId, detailId);

                                        if (null != carDetailDO) {
                                            // 读一次，写一次
                                            carDetailDOMapper.insert(carDetailDO);

                                            // 统计price
                                            statisticsPrice(carDetailDO.getPrice(), minMaxPrice);

                                            // 统计seatNum
                                            statisticsSeatNum(carDetailDO.getSeatNum(), seatNumSet);
                                        }
                                    }


                                });

                        // 补充price
                        fillCarModelPrice(m, minMaxPrice);

                        // 补充seatNum
                        fillCarModelSeatNum(m, seatNumSet);

                        // 清空容器
                        minMaxPrice.clear();
                        seatNumSet.clear();
                    }

                });

    }

    /**
     * 统计price（min/max）
     *
     * @param currentPrice
     * @param minMaxPriceList
     */
    private void statisticsPrice(String currentPrice, List<Double> minMaxPriceList) {
        if (StringUtils.isNotBlank(currentPrice) && !currentPrice.contains("无")) {
            Double price = Double.valueOf(currentPrice.split("万")[0]);

            if (CollectionUtils.isEmpty(minMaxPriceList)) {
                minMaxPriceList.add(price);
                minMaxPriceList.add(price);
            } else {
                Double minPrice = minMaxPriceList.get(0);
                Double maxPrice = minMaxPriceList.get(1);

                minPrice = price < minPrice ? price : minPrice;
                maxPrice = price > maxPrice ? price : minPrice;

                minMaxPriceList.set(0, minPrice);
                minMaxPriceList.set(1, maxPrice);
            }
        }
    }

    /**
     * 统计座位数 （子车型所有座位数）
     *
     * @param currentSeatNum
     * @param seatNumSet
     */
    private void statisticsSeatNum(Integer currentSeatNum, Set<Integer> seatNumSet) {
        if (null != currentSeatNum) {
            seatNumSet.add(currentSeatNum);
        }
    }


    /**
     * 补充车系price（官方指导价）
     *
     * @param carModelDO
     * @param minMaxPriceList
     */
    private void fillCarModelPrice(CarModelDO carModelDO, List<Double> minMaxPriceList) {
        if (!CollectionUtils.isEmpty(minMaxPriceList)) {
            CarModelDO updateCarModelDO = new CarModelDO();
            updateCarModelDO.setId(carModelDO.getId());

            Double minPrice = minMaxPriceList.get(0);
            Double maxPrice = minMaxPriceList.get(1);

            if (minPrice.equals(maxPrice)) {
                updateCarModelDO.setPrice(minPrice + "万");
            } else {
                updateCarModelDO.setPrice(minPrice + "-" + maxPrice + "万");
            }

            // 编辑price
            carModelDOMapper.updateByPrimaryKeySelective(updateCarModelDO);
        }
    }

    /**
     * 补充车系seatNum
     *
     * @param carModelDO
     * @param seatNumSet
     */
    private void fillCarModelSeatNum(CarModelDO carModelDO, Set<Integer> seatNumSet) {
        if (!CollectionUtils.isEmpty(seatNumSet)) {
            CarModelDO updateCarModelDO = new CarModelDO();
            updateCarModelDO.setId(carModelDO.getId());

            String seatNumStr = seatNumSet.stream()
                    .map(e -> {
                        return e.toString();
                    })
                    .collect(Collectors.joining("/"));

            updateCarModelDO.setSeatNum(seatNumStr);

            // 编辑price
            carModelDOMapper.updateByPrimaryKeySelective(updateCarModelDO);
        }
    }


    /**
     * 获取汽车品牌数据
     */
    public List<CarBrandDO> getCarBrand() {

        String path = "/car/brand";
        String method = "GET";

        // headers
        Map<String, String> headers = new HashMap<>();
        // 最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);

        // query
        Map<String, String> querys = new HashMap<>();

        List<CarBrandDO> carBrandDOS = requestAndParseBrand(host, path, method, headers, querys);
        return carBrandDOS;
    }


    /**
     * 插入，并获取车系数据
     *
     * @param carBrandDOS
     * @param modelIdDetailIdsMap 车系ID-车型ID列表映射
     */
    public List<CarModelDO> insertAndGetCarModel(List<CarBrandDO> carBrandDOS, Map<Long, List<Long>> modelIdDetailIdsMap) {

        String path = "/car/carlist";
        String method = "GET";

        // headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode);

        // query
        Map<String, String> querys = new HashMap<>();


        // exist
        List<Long> existModelIdList = carModelDOMapper.getAllId(null);

        List<CarModelDO> carModelDOList = new ArrayList<>();
        carBrandDOS.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    querys.put("parentid", e.getId().toString());

                    List<CarModelDO> carModelDOS = requestAndParseModel(host, path, method, headers, querys, modelIdDetailIdsMap, existModelIdList);
                    if (!CollectionUtils.isEmpty(carModelDOS)) {
                        // 读一次，写一次
                        carModelDOMapper.batchInsert(carModelDOS);
                        // 记录，待更新price、seatNum
                        carModelDOList.addAll(carModelDOS);
                    }

                    wait_();
                });

        return carModelDOList;
    }

    /**
     * 接口访问过快问题
     */
    private void wait_() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

    /**
     * 请求并解析车系数据
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param modelIdDetailIdsMap 车系ID-车型ID列表映射
     * @param existModelIdList
     * @return
     */
    private List<CarModelDO> requestAndParseModel(String host, String path, String method, Map<String, String> headers,
                                                  Map<String, String> querys, Map<Long, List<Long>> modelIdDetailIdsMap,
                                                  List<Long> existModelIdList) {
        try {

            JSONObject bodyObj = requestAndCheckThenReturnResult(host, path, method, headers, querys);
            if (CollectionUtils.isEmpty(bodyObj)) {
                return null;
            }

            Long brandId = Long.valueOf(querys.get("parentid"));
            // 车系列表容器
            List<CarModelDO> carModelDOS = Lists.newArrayList();

            JSONArray resultJsonArr = bodyObj.getJSONArray("result");
            if (CollectionUtils.isEmpty(resultJsonArr)) {
                return null;
            }

            resultJsonArr.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        // 子公司   depth = 2
                        JSONObject eObj = (JSONObject) e;
                        // 子公司名称(生产厂商)
                        String productionFirm = eObj.getString("name");

                        // 车系列表    carlist : depth = 3
                        JSONArray modelList = eObj.getJSONArray("carlist");

                        if (!CollectionUtils.isEmpty(modelList)) {

                            modelList.stream()
                                    .filter(Objects::nonNull)
                                    .forEach(m -> {

                                        JSONObject mObj = (JSONObject) m;
                                        Long modelId = mObj.getLong("id");

                                        // 不存在，才解析加入
                                        if (!existModelIdList.contains(modelId)) {

                                            // 解析并填充结果到Model对象
                                            CarModelDO carModelDO = parseAndFillModel(mObj, brandId, modelId, productionFirm);

                                            // add-Model
                                            carModelDOS.add(carModelDO);

                                            // 记录id映射
                                            recordIdMapping(mObj, modelId, modelIdDetailIdsMap);
                                        }

                                    });

                        }


                    });

            return carModelDOS;

        } catch (Exception e) {
            logger.error(path + "：接口调用失败！", e);
//            throw new RuntimeException(path + "：获取车型品牌接口调用失败！", e);
            return null;
        }

    }

    /**
     * 解析并填充结果到Model对象
     *
     * @param mObj
     * @param brandId
     * @param modelId
     * @param productionFirm
     * @return
     */
    private CarModelDO parseAndFillModel(JSONObject mObj, Long brandId, Long modelId, String productionFirm) {
        CarModelDO carModelDO = new CarModelDO();
        carModelDO.setBrandId(brandId);
        carModelDO.setProductionFirm(productionFirm);

        carModelDO.setId(modelId);
        carModelDO.setName(mObj.getString("name"));
        carModelDO.setFullName(mObj.getString("fullname"));
        carModelDO.setInitial(mObj.getString("initial"));
        carModelDO.setLogo(mObj.getString("logo"));

        String saleStateStr = mObj.getString("salestate");
        carModelDO.setSaleState(saleStateMap.get(saleStateStr));

        // detailList 的两个极值
        carModelDO.setPrice(null);
        carModelDO.setSeatNum(null);

        // 暂无
        carModelDO.setProductionType(null);

        // 人工填写
        carModelDO.setSeriesCode(null);
        carModelDO.setMnemonicCode(null);

        // 时间、状态
        carModelDO.setGmtCreate(new Date());
        carModelDO.setGmtModify(new Date());
        carModelDO.setStatus(VALID_STATUS);

        return carModelDO;
    }

    /**
     * 记录ID映射
     *
     * @param mObj
     * @param modelId
     * @param modelIdDetailIdsMap
     */
    private void recordIdMapping(JSONObject mObj, Long modelId, Map<Long, List<Long>> modelIdDetailIdsMap) {

        // 车系列表     list : depth = 4
        JSONArray detailList = mObj.getJSONArray("list");

        // 记录车系-车型ID映射
        if (!CollectionUtils.isEmpty(detailList)) {
            detailList.stream()
                    .filter(Objects::nonNull)
                    .forEach(d -> {

                        JSONObject dObj = (JSONObject) d;

                        Long detailId = dObj.getLong("id");
                        if (!modelIdDetailIdsMap.containsKey(modelId)) {
                            List<Long> detailIds = Lists.newArrayList(detailId);
                            modelIdDetailIdsMap.put(modelId, detailIds);
                        } else {
                            modelIdDetailIdsMap.get(modelId).add(detailId);
                        }

                    });
        }
    }

    /**
     * 请求车系大全API服务，并校验结果，返回有效结果
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @return
     * @throws Exception
     */
    private JSONObject requestAndCheckThenReturnResult(String host, String path, String method,
                                                       Map<String, String> headers,
                                                       Map<String, String> querys) throws Exception {

        // 请求
        HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
        // 获取response的body
        String body = EntityUtils.toString(response.getEntity());

        // 解析body
        JSONObject bodyObj = JSON.parseObject(body);
        if (CollectionUtils.isEmpty(bodyObj)) {
            // 空结果返回，记录path和querys
            logger.error("path : " + path + " ,   querys : " + JSON.toJSONString(querys));
            return null;
        }

        // status=0，表示成功
        Integer status = bodyObj.getInteger("status");

        //  201：上级ID错误；   202：车型ID错误；   205：无数据                         eg:  body：{"msg":"没有信息","result":"","status":"205"}
        if (null == status || !status.equals(0)) {
            // status异常问题   暂时先记录一下，看看都有哪些异常
            logger.error("status = " + status + " >>>>>> " + NEW_LINE +
                    "path : " + path + " ,   querys : " + JSON.toJSONString(querys) + " >>>>>> " + NEW_LINE +
                    JSON.toJSONString(bodyObj) + NEW_LINE);
            return null;
        }

        return bodyObj;
    }


    /**
     * 获取车型详情
     *
     * @param modelId  车系ID
     * @param detailId 车型ID
     */
    public CarDetailDO getCarDetail(Long modelId, Long detailId) {

        String path = "/car/detail";
        String method = "GET";

        // headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode);

        // query
        Map<String, String> querys = new HashMap<>();
        querys.put("carid", detailId.toString());

        CarDetailDO carDetailDO = requestAndParseDetail(host, path, method, headers, querys, modelId);

        wait_();

        return carDetailDO;
    }

    /**
     * 请求并解析车型数据
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param modelId
     * @return
     */
    private CarDetailDO requestAndParseDetail(String host, String path, String method, Map<String, String> headers,
                                              Map<String, String> querys, Long modelId) {

        try {

            JSONObject bodyObj = requestAndCheckThenReturnResult(host, path, method, headers, querys);
            if (CollectionUtils.isEmpty(bodyObj)) {
                return null;
            }
            JSONObject resultJObj = bodyObj.getJSONObject("result");
            if (CollectionUtils.isEmpty(resultJObj)) {
                return null;
            }

            CarDetailDO carDetailDO = new CarDetailDO();
            carDetailDO.setModelId(modelId);
            carDetailDO.setId(resultJObj.getLong("id"));
            carDetailDO.setName(resultJObj.getString("name"));
            carDetailDO.setInitial(resultJObj.getString("initial"));
            carDetailDO.setLogo(resultJObj.getString("logo"));
            carDetailDO.setPrice(resultJObj.getString("price"));
            carDetailDO.setYearType(resultJObj.getString("yeartype"));
            carDetailDO.setSizeType(resultJObj.getString("sizetype"));

            // convert
            carDetailDO.setSaleState(saleStateMap.get(resultJObj.getString("salestate")));
            carDetailDO.setProductionState(productionStateMap.get(resultJObj.getString("productionstate")));

            String salePrice = resultJObj.getJSONObject("basic").getString("saleprice");
            carDetailDO.setSalePrice(salePrice);
            //String
            Integer seatNum = resultJObj.getJSONObject("basic").getInteger("seatnum");
            carDetailDO.setSeatNum(seatNum);

            Integer doorNum = resultJObj.getJSONObject("body").getInteger("doornum");
            carDetailDO.setDoorNum(doorNum);

            String fuelType = resultJObj.getJSONObject("engine").getString("fueltype");
            carDetailDO.setFuelType(fuelTypeMap.get(fuelType));

            // 时间、状态
            carDetailDO.setGmtCreate(new Date());
            carDetailDO.setGmtModify(new Date());
            carDetailDO.setStatus(VALID_STATUS);

            return carDetailDO;

        } catch (Exception e) {
            logger.error(path + "：接口调用失败！", e);
//            throw new RuntimeException(path + "：获取车型详情接口调用失败！", e);
            return null;
        }

    }

    /**
     * 请求并解析汽车品牌数据
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     */
    private List<CarBrandDO> requestAndParseBrand(String host, String path, String method, Map<String, String> headers, Map<String, String> querys) {
        try {

            JSONObject bodyObj = requestAndCheckThenReturnResult(host, path, method, headers, querys);

            JSONArray resultJsonArr = bodyObj.getJSONArray("result");
            List<CarBrandDO> carBrandDOS = resultJsonArr.stream()
                    .map(e -> {

                        JSONObject eObj = (JSONObject) e;

                        CarBrandDO carBrandDO = new CarBrandDO();
                        carBrandDO.setId(eObj.getLong("id"));
                        carBrandDO.setName(eObj.getString("name"));
                        carBrandDO.setInitial(eObj.getString("initial"));
                        carBrandDO.setLogo(eObj.getString("logo"));

                        // 时间、状态
                        carBrandDO.setGmtCreate(new Date());
                        carBrandDO.setGmtModify(new Date());
                        carBrandDO.setStatus(VALID_STATUS);

                        return carBrandDO;
                    })
                    .collect(Collectors.toList());

            return carBrandDOS;

        } catch (Exception e) {
            logger.error(path + "：接口调用失败！", e);
//            throw new RuntimeException(path + "：获取车型品牌接口调用失败！", e);
            return null;
        }

    }

    /**
     * 车系ID-车系实体 映射关系： model_id —— CarModellDO 映射
     *
     * @return
     */
    public ConcurrentMap<Long, CarModelDO> getIdModelDOMapping() {
        ConcurrentMap<Long, CarModelDO> idModelDOMap = Maps.newConcurrentMap();
        List<CarModelDO> allCarModelDO = carModelDOMapper.getAll(null);
        allCarModelDO.parallelStream()
                .filter(e -> null != e && null != e.getId())
                .forEach(e -> {
                    idModelDOMap.put(e.getId(), e);
                });
        return idModelDOMap;
    }

    /**
     * 车型ID-车型实体 映射关系： detail_id —— CarDetailDO 映射
     *
     * @return
     */
    public ConcurrentMap<Long, CarDetailDO> getIdDetailDOMapping() {
        ConcurrentMap<Long, CarDetailDO> idDetailDOMap = Maps.newConcurrentMap();
        List<CarDetailDO> allCarDetailDO = carDetailDOMapper.getAll(null);
        allCarDetailDO.parallelStream()
                .filter(e -> null != e && null != e.getId())
                .forEach(e -> {
                    idDetailDOMap.put(e.getId(), e);
                });
        return idDetailDOMap;
    }

    /**
     * 梳理id映射关系： model_id——detail_id列表  映射
     *
     * @param carDetailDOS
     * @return
     */
    private ConcurrentMap<Long, List<Long>> getModelIdDetailIdsMapping(List<CarDetailDO> carDetailDOS) {
        ConcurrentMap<Long, List<Long>> modelIdDetailIdsMap = Maps.newConcurrentMap();

        carDetailDOS.parallelStream()
                .filter(e -> null != e && null != e.getId() && null != e.getModelId())
                .forEach(e -> {

                    Long modelId = e.getModelId();
                    Long detailId = e.getId();

                    if (!modelIdDetailIdsMap.containsKey(modelId)) {
                        modelIdDetailIdsMap.put(modelId, Lists.newArrayList(detailId));
                    } else {
                        modelIdDetailIdsMap.get(modelId).add(detailId);
                    }

                });

        return modelIdDetailIdsMap;
    }
}
