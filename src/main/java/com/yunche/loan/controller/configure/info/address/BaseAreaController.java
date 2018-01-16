package com.yunche.loan.controller.configure.info.address;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.dto.configure.info.address.BaseAreaDTO;
import com.yunche.loan.obj.configure.info.address.BaseAreaDO;
import com.yunche.loan.query.configure.info.address.BaseAreaQuery;
import com.yunche.loan.result.ResultBean;
import com.yunche.loan.service.configure.info.address.BaseAreaService;
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
    public ResultBean<Void> delete(@RequestParam("id") Integer id) {
        logger.info(Arrays.asList("delete", id.toString()).stream().collect(Collectors.joining("-")));
        return baseAreaService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<BaseAreaDTO> getById(@RequestParam("id") Integer id) {
        logger.info(Arrays.asList("getById", id.toString()).stream().collect(Collectors.joining("-")));
        return baseAreaService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<BaseAreaDTO> query(@RequestBody BaseAreaQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("-")));
        return baseAreaService.query(query);
    }

}
