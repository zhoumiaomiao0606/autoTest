package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.dataObj.CarBrandDO;
import com.yunche.loan.domain.viewObj.CarBrandVO;
import com.yunche.loan.service.CarBrandService;
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
@RequestMapping("/car/brand")
public class CarBrandController {

    private static final Logger logger = LoggerFactory.getLogger(CarBrandController.class);

    @Autowired
    private CarBrandService carBrandService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody CarBrandDO carBrandDO) {
        logger.info(Arrays.asList("create", JSON.toJSONString(carBrandDO)).stream().collect(Collectors.joining("-")));
        return carBrandService.create(carBrandDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody CarBrandDO carBrandDO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(carBrandDO)).stream().collect(Collectors.joining("-")));
        return carBrandService.update(carBrandDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(id)).stream().collect(Collectors.joining("-")));
        return carBrandService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<CarBrandVO> getById(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("getById", JSON.toJSONString(id)).stream().collect(Collectors.joining("-")));
        return carBrandService.getById(id);
    }

    /**
     * 获取所有品牌
     *
     * @return
     */
    @GetMapping("/list")
    public ResultBean<List<CarBrandVO>> listAll() {
        logger.info("list");
        return carBrandService.listAll();
    }

}
