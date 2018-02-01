package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.CarModelQuery;
import com.yunche.loan.domain.dataObj.CarModelDO;
import com.yunche.loan.domain.viewObj.CarModelVO;
import com.yunche.loan.service.CarModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@CrossOrigin
@RestController
@RequestMapping("/car/model")
public class CarModelController {

    private static final Logger logger = LoggerFactory.getLogger(CarModelController.class);

    @Autowired
    private CarModelService carModelService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody CarModelDO carModelDO) {
        logger.info(Arrays.asList("create", JSON.toJSONString(carModelDO)).stream().collect(Collectors.joining("-")));
        return carModelService.create(carModelDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody CarModelDO carModelDO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(carModelDO)).stream().collect(Collectors.joining("-")));
        return carModelService.update(carModelDO);
    }

    /**
     * 逻辑删除
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(id)).stream().collect(Collectors.joining("-")));
        return carModelService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<CarModelVO> getById(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("getById", JSON.toJSONString(id)).stream().collect(Collectors.joining("-")));
        return carModelService.getById(id);
    }

    /**
     * 分页查询
     * <p>
     * 品牌ID必传
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<CarModelVO>> query(@RequestBody CarModelQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("-")));
        return carModelService.query(query);
    }

}
