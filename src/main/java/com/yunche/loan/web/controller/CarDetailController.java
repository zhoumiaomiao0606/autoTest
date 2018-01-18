package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.CarDetailQuery;
import com.yunche.loan.domain.QueryObj.InsuranceCompanyQuery;
import com.yunche.loan.domain.dataObj.CarDetailDO;
import com.yunche.loan.domain.dataObj.InsuranceCompanyDO;
import com.yunche.loan.domain.valueObj.CarDetailVO;
import com.yunche.loan.domain.valueObj.InsuranceCompanyVO;
import com.yunche.loan.service.CarDetailService;
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
@RequestMapping("/car/series")
public class CarDetailController {

    private static final Logger logger = LoggerFactory.getLogger(CarDetailController.class);

    @Autowired
    private CarDetailService carDetailService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Long> create(@RequestBody CarDetailDO carDetailDO) {
        logger.info(Arrays.asList("create", JSON.toJSONString(carDetailDO)).stream().collect(Collectors.joining("-")));
        return carDetailService.create(carDetailDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> update(@RequestBody CarDetailDO carDetailDO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(carDetailDO)).stream().collect(Collectors.joining("-")));
        return carDetailService.update(carDetailDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("delete", id.toString()).stream().collect(Collectors.joining("-")));
        return carDetailService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<CarDetailVO> getById(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("getById", id.toString()).stream().collect(Collectors.joining("-")));
        return carDetailService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<List<CarDetailVO>> query(@RequestBody CarDetailQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("-")));
        return carDetailService.query(query);
    }


}
