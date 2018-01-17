package com.yunche.loan.service.impl.configure.info.car;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yunche.loan.common.HttpUtils;
import com.yunche.loan.obj.configure.info.car.CarBrandDO;
import com.yunche.loan.obj.configure.info.car.CarDetailDO;
import com.yunche.loan.obj.configure.info.car.CarModelDO;
import com.yunche.loan.result.ResultBean;
import com.yunche.loan.service.configure.info.car.CarBrandService;
import com.yunche.loan.service.configure.info.car.CarModelService;
import com.yunche.loan.service.configure.info.car.CarDetailService;
import com.yunche.loan.service.configure.info.car.CarService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static com.yunche.loan.constant.configure.info.car.CarConst.fuelTypeMap;
import static com.yunche.loan.constant.configure.info.car.CarConst.productionStateMap;
import static com.yunche.loan.constant.configure.info.car.CarConst.saleStateMap;

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
    private static final String NEW_LINE = System.getProperty("line.separator");


    /**
     * 阿里云车型大全API服务——HOST
     * tips：接口限流 30次/min     ===> 大概估算一下：跑一次大概近3个小时
     */
    private String host = "http://jisucxdq.market.alicloudapi.com";
    /**
     * 阿里云车型大全API服务——appcode
     */
    private String appcode = "567ef51853094159a974a2955f312590";


    @Autowired
    CarBrandService carBrandService;
    @Autowired
    CarModelService carModelService;
    @Autowired
    CarDetailService carDetailService;

    @Override
    public ResultBean<Void> importCar() {
        long startTime = System.currentTimeMillis();

        // 车系ID-车型ID列表映射  ——  k/v : modelId / detailIdList
        Map<Integer, List<Integer>> modelIdDetailIdsMap = Maps.newHashMap();

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
        logger.info("/car/import：导入车型库总耗时 : {}h", new BigDecimal(totalTime).doubleValue() / 3600);

        return ResultBean.ofSuccess(null, "导入成功");
    }

    @Override
    public ResultBean<Void> fillModel() {
        long startTime = System.currentTimeMillis();

        // 获取所有 model_id —— detail_id
        List<CarDetailDO> carDetailDOS = carDetailService.getAllIdAndModelId();
        logger.info("car_detail表数据总量为 : " + carDetailDOS.size());
        if (CollectionUtils.isEmpty(carDetailDOS)) {
            return ResultBean.ofSuccess(null, "car_detail表为空表,无可更新数据.");
        }

        // 梳理id映射关系： model_id —— detail_id列表
        ConcurrentMap<Integer, List<Integer>> modelIdDetailIdsMap = getModelIdDetailIdsMapping(carDetailDOS);

        // 执行补偿任务
        execFillModel(modelIdDetailIdsMap);

        long totalTime = System.currentTimeMillis() - startTime;
        logger.info("/car/fillModel：车系表补偿任务总耗时 : {}h", new BigDecimal(totalTime).doubleValue() / 3600);

        return ResultBean.ofSuccess(null, "补偿任务执行完成");
    }

    @Override
    public ResultBean<String> count() {
        // 获取品牌数据
        logger.info("查询品牌开始   >>>>>   ");
        List<CarBrandDO> carBrandDOS = getCarBrand();
        logger.info("查询品牌完成   >>>>>   ");

        logger.info("统计数量开始   >>>>>   ");
        Map<String, Integer> countMap = countTotal(carBrandDOS);
        logger.info("统计数量完成   >>>>>   ");

        return ResultBean.ofSuccess(JSON.toJSONString(countMap));
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
     * @param modelIdDetailIdsMap
     */
    private void execFillModel(ConcurrentMap<Integer, List<Integer>> modelIdDetailIdsMap) {
        // 容器
        ArrayList<Double> minMaxPrice = Lists.newArrayList();
        Set<Integer> seatNumSet = Sets.newTreeSet();

        modelIdDetailIdsMap.forEach((modelId, detailIds) -> {

            detailIds.parallelStream()
                    .forEach(detailId -> {

                        CarDetailDO carDetailDO = carDetailService.getById(detailId);
                        if (null != carDetailDO) {
                            // 统计price
                            statisticsPrice(carDetailDO.getPrice(), minMaxPrice);
                            // 统计seatNum
                            statisticsSeatNum(carDetailDO.getSeatNum(), seatNumSet);
                        }

                    });

            // 获取当前要补偿数据的CarModelDO
            CarModelDO carModelDO = carModelService.getById(modelId);
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

    /**
     * 梳理id映射关系： model_id——detail_id列表  映射
     *
     * @param carDetailDOS
     * @return
     */
    private ConcurrentMap<Integer, List<Integer>> getModelIdDetailIdsMapping(List<CarDetailDO> carDetailDOS) {
        ConcurrentMap<Integer, List<Integer>> modelIdDetailIdsMap = Maps.newConcurrentMap();

        carDetailDOS.parallelStream()
                .filter(e -> null != e && null != e.getId() && null != e.getModelId())
                .forEach(e -> {

                    Integer modelId = e.getModelId();
                    Integer detailId = e.getId();

                    if (!modelIdDetailIdsMap.containsKey(modelId)) {
                        modelIdDetailIdsMap.put(modelId, Lists.newArrayList(detailId));
                    } else {
                        modelIdDetailIdsMap.get(modelId).add(detailId);
                    }

                });

        return modelIdDetailIdsMap;
    }

    private void insertBrand(List<CarBrandDO> carBrandDOS) {

        int totalNum = carBrandDOS.size();

        // 获取已存在
        List<Integer> existBrandIdList = carBrandService.getAllId();
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
            Integer count = carBrandService.batchInsert(notExistCarBrandDOS);
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
    private void insertCarDetailAndFillCarModel(List<CarModelDO> needFillCarModelDOS, Map<Integer, List<Integer>> modelIdDetailIdsMap) {
        if (CollectionUtils.isEmpty(modelIdDetailIdsMap)) {
            return;
        }

        // exist
        List<Integer> existCarDetailIds = carDetailService.getAllId();

        // 极值容器
        List<Double> minMaxPrice = Lists.newArrayList();
        Set<Integer> seatNumSet = Sets.newHashSet();

        needFillCarModelDOS.stream()
                .filter(m -> null != m && null != m.getId())
                .forEach(m -> {

                    Integer modelId = m.getId();
                    List<Integer> detailIds = modelIdDetailIdsMap.get(modelId);
                    if (!CollectionUtils.isEmpty(detailIds)) {

                        detailIds.stream()
                                .forEach(detailId -> {

                                    // 不存在，则插入
                                    if (!existCarDetailIds.contains(detailId)) {
                                        // 获取车型数据
                                        CarDetailDO carDetailDO = getCarDetail(modelId, detailId);

                                        if (null != carDetailDO) {
                                            // 读一次，写一次
                                            carDetailService.insert(carDetailDO);

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
            carModelService.updateSelective(updateCarModelDO);
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
            carModelService.updateSelective(updateCarModelDO);
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
    public List<CarModelDO> insertAndGetCarModel(List<CarBrandDO> carBrandDOS, Map<Integer, List<Integer>> modelIdDetailIdsMap) {

        String path = "/car/carlist";
        String method = "GET";

        // headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode);

        // query
        Map<String, String> querys = new HashMap<>();


        // exist
        List<Integer> existModelIdList = carModelService.getAllId();

        List<CarModelDO> carModelDOList = new ArrayList<>();
        carBrandDOS.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    querys.put("parentid", e.getId().toString());

                    List<CarModelDO> carModelDOS = requestAndParseModel(host, path, method, headers, querys, modelIdDetailIdsMap, existModelIdList);
                    if (!CollectionUtils.isEmpty(carModelDOS)) {
                        // 读一次，写一次
                        carModelService.batchInsert(carModelDOS);
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
                                                  Map<String, String> querys, Map<Integer, List<Integer>> modelIdDetailIdsMap,
                                                  List<Integer> existModelIdList) {
        try {

            JSONObject bodyObj = requestAndCheckThenReturnResult(host, path, method, headers, querys);
            if (CollectionUtils.isEmpty(bodyObj)) {
                return null;
            }

            Integer brandId = Integer.valueOf(querys.get("parentid"));
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
                                        Integer modelId = mObj.getInteger("id");

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
    private CarModelDO parseAndFillModel(JSONObject mObj, Integer brandId, Integer modelId, String productionFirm) {
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

        return carModelDO;
    }

    /**
     * 记录ID映射
     *
     * @param mObj
     * @param modelId
     * @param modelIdDetailIdsMap
     */
    private void recordIdMapping(JSONObject mObj, Integer modelId, Map<Integer, List<Integer>> modelIdDetailIdsMap) {

        // 车系列表     list : depth = 4
        JSONArray detailList = mObj.getJSONArray("list");

        // 记录车系-车型ID映射
        if (!CollectionUtils.isEmpty(detailList)) {
            detailList.stream()
                    .filter(Objects::nonNull)
                    .forEach(d -> {

                        JSONObject dObj = (JSONObject) d;

                        Integer detailId = dObj.getInteger("id");
                        if (!modelIdDetailIdsMap.containsKey(modelId)) {
                            List<Integer> detailIds = Lists.newArrayList(detailId);
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
    private JSONObject requestAndCheckThenReturnResult(String host, String path, String method, Map<String, String> headers, Map<String, String> querys) throws Exception {

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
    public CarDetailDO getCarDetail(Integer modelId, Integer detailId) {

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
                                              Map<String, String> querys, Integer modelId) {

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
            carDetailDO.setId(resultJObj.getInteger("id"));
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
            List<CarBrandDO> carBrandDOS = resultJsonArr.parallelStream()
                    .map(e -> {

                        JSONObject eObj = (JSONObject) e;

                        CarBrandDO carBrandDO = new CarBrandDO();
                        carBrandDO.setId(eObj.getInteger("id"));
                        carBrandDO.setName(eObj.getString("name"));
                        carBrandDO.setInitial(eObj.getString("initial"));
                        carBrandDO.setLogo(eObj.getString("logo"));

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

}
