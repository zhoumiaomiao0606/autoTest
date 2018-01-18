package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.domain.valueObj.BaseAreaVO;
import com.yunche.loan.domain.dataObj.BaseAreaDO;
import com.yunche.loan.domain.QueryObj.BaseAreaQuery;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.BaseAreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@CrossOrigin
@RestController
@RequestMapping("/area")
public class BaseAreaController {

    private static final Logger logger = LoggerFactory.getLogger(BaseAreaController.class);

    @Autowired
    private BaseAreaService baseAreaService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> create(@RequestBody BaseAreaDO baseAreaDO) {
        logger.info(Arrays.asList("create", JSON.toJSONString(baseAreaDO)).stream().collect(Collectors.joining("-")));
        return baseAreaService.create(baseAreaDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> update(@RequestBody BaseAreaDO baseAreaDO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(baseAreaDO)).stream().collect(Collectors.joining("-")));
        return baseAreaService.update(baseAreaDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long areaId) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(areaId)).stream().collect(Collectors.joining("-")));
        return baseAreaService.delete(areaId);
    }

    @GetMapping("/getById")
    public ResultBean<BaseAreaVO> getById(@RequestParam("id") Long areaId) {
        logger.info(Arrays.asList("getById", JSON.toJSONString(areaId)).stream().collect(Collectors.joining("-")));
        return baseAreaService.getById(areaId);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<BaseAreaVO> query(@RequestBody BaseAreaQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("-")));
        return baseAreaService.query(query);
    }

}
