package com.yunche.loan.service.impl.configure.info.car;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

import java.util.*;
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
        // 车型ID-车款ID列表映射  ——  k/v : modelId / detailIdList
        Map<Integer, List<Integer>> modelIdDetailIdsMap = Maps.newHashMap();

        // 获取品牌数据
        logger.info("查询品牌开始" + NEW_LINE);
        List<CarBrandDO> carBrandDOS = getCarBrand();
        logger.info("查询品牌完成" + NEW_LINE);

        logger.info("插入品牌数据开始   >>>>>   ");
        insertBrand(carBrandDOS);
        logger.info("插入品牌数据完成   >>>>>   ");


        // 获取车型数据
        logger.info("查询车型开始   >>>>>   ");
        List<CarModelDO> needFillCarModelDOS = insertAndGetCarModel(carBrandDOS, modelIdDetailIdsMap);
        logger.info("查询车型完成   >>>>>   ");

        // 获取车款数据，并补充车型数据(price、seatNum)
        logger.info("查询车款开始   >>>>>   ");
        insertCarDetailAndFillCarModel(needFillCarModelDOS, modelIdDetailIdsMap);
        logger.info("查询车款完成   >>>>>   ");

        return ResultBean.ofSuccess(null, "导入成功");
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
     * 获取车款数据，并补充车型数据(price、seatNum)
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
        Double[] minMaxPriceArr = new Double[2];
        Integer[] minMaxSeatNumArr = new Integer[2];

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
                                        // 获取车款数据
                                        CarDetailDO carDetailDO = getCarDetail(modelId, detailId);

                                        if (null != carDetailDO) {
                                            // 读一次，写一次
                                            carDetailService.insert(carDetailDO);

                                            // 统计price
                                            statisticsPrice(carDetailDO.getPrice(), minMaxPriceArr);

                                            // 统计seatNum
                                            statisticsSeatNum(carDetailDO.getSeatNum(), minMaxSeatNumArr);
                                        }
                                    }


                                });

                        // 补充price
                        fillCarModelPrice(m, minMaxPriceArr);

                        // 补充seatNum
                        fillCarModelSeatNum(m, minMaxSeatNumArr);
                    }

                });

    }

    /**
     * 统计price（min/max）
     *
     * @param currentPrice
     * @param minMaxPriceArr
     */
    private void statisticsPrice(String currentPrice, Double[] minMaxPriceArr) {
        if (StringUtils.isNotBlank(currentPrice) && !currentPrice.contains("无")) {
            Double price = Double.valueOf(currentPrice.split("万")[0]);

            if (null == minMaxPriceArr[0]) {
                minMaxPriceArr[0] = price;
                minMaxPriceArr[1] = price;
            } else {
                Double minPrice = minMaxPriceArr[0];
                Double maxPrice = minMaxPriceArr[1];

                minMaxPriceArr[0] = price < minPrice ? price : minPrice;
                minMaxPriceArr[1] = price > maxPrice ? price : minPrice;
            }
        }

    }

    /**
     * 统计座位数（min/max）
     *
     * @param currentSeatNum
     * @param minMaxSeatNumArr
     */
    private void statisticsSeatNum(Integer currentSeatNum, Integer[] minMaxSeatNumArr) {

        if (null != currentSeatNum) {
            if (null == minMaxSeatNumArr[0]) {
                minMaxSeatNumArr[0] = currentSeatNum;
                minMaxSeatNumArr[1] = currentSeatNum;
            } else {
                Integer minSeatNum = minMaxSeatNumArr[0];
                Integer maxSeatNum = minMaxSeatNumArr[1];

                minMaxSeatNumArr[0] = currentSeatNum < minSeatNum ? currentSeatNum : minSeatNum;
                minMaxSeatNumArr[1] = currentSeatNum > maxSeatNum ? currentSeatNum : maxSeatNum;
            }
        }
    }


    /**
     * 补充车型price（官方指导价）
     *
     * @param carModelDO
     * @param minMaxPriceArr
     */
    private void fillCarModelPrice(CarModelDO carModelDO, Double[] minMaxPriceArr) {
        if (null != minMaxPriceArr[0]) {
            CarModelDO updateCarModelDO = new CarModelDO();
            updateCarModelDO.setId(carModelDO.getId());

            Double minPrice = minMaxPriceArr[0];
            Double maxPrice = minMaxPriceArr[1];

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
     * 补充车型seatNum
     *
     * @param carModelDO
     * @param minMaxSeatNumArr
     */
    private void fillCarModelSeatNum(CarModelDO carModelDO, Integer[] minMaxSeatNumArr) {
        if (null != minMaxSeatNumArr[0]) {
            CarModelDO updateCarModelDO = new CarModelDO();
            updateCarModelDO.setId(carModelDO.getId());

            Integer minSeatNum = minMaxSeatNumArr[0];
            Integer maxSeatNum = minMaxSeatNumArr[1];

            if (minSeatNum.equals(maxSeatNum)) {
                updateCarModelDO.setSeatNum(minSeatNum.toString());
            } else {
                updateCarModelDO.setPrice(minSeatNum + "/" + maxSeatNum);
            }

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
     * 插入，并获取车型数据
     *
     * @param carBrandDOS
     * @param modelIdDetailIdsMap 车型ID-车款ID列表映射
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
     * 请求并解析车型数据
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param modelIdDetailIdsMap 车型ID-车款ID列表映射
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
            // 车型列表容器
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

                        // 车型列表    carlist : depth = 3
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
            logger.error(path + "：获取车型品牌接口调用失败！", e);
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

        // 车款列表     list : depth = 4
        JSONArray detailList = mObj.getJSONArray("list");

        // 记录车型-车款ID映射
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
     * 请求车型大全API服务，并校验结果，返回有效结果
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
     * 获取车款详情
     *
     * @param modelId  车型ID
     * @param detailId 车款ID
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
     * 请求并解析车款数据
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

            Integer seatNum = resultJObj.getJSONObject("basic").getInteger("seatnum");
            carDetailDO.setSeatNum(seatNum);

            Integer doorNum = resultJObj.getJSONObject("body").getInteger("doornum");
            carDetailDO.setDoorNum(doorNum);

            String fuelType = resultJObj.getJSONObject("engine").getString("fueltype");
            carDetailDO.setFuelType(fuelTypeMap.get(fuelType));

            return carDetailDO;

        } catch (Exception e) {
            logger.error(path + "：获取车款详情接口调用失败！", e);
//            throw new RuntimeException(path + "：获取车款详情接口调用失败！", e);
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
            logger.error(path + "：获取车型品牌接口调用失败！", e);
//            throw new RuntimeException(path + "：获取车型品牌接口调用失败！", e);
            return null;
        }

    }


}
