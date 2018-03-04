package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.AppVersionDO;
import com.yunche.loan.domain.query.AppVersionQuery;
import com.yunche.loan.domain.vo.AppVersionVO;
import com.yunche.loan.service.AppVersionService;
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
 * @date 2018/2/12
 */
@CrossOrigin
@RestController
@RequestMapping("/app/version")
public class AppVersionController {

    private static final Logger logger = LoggerFactory.getLogger(AppVersionController.class);

    @Autowired
    private AppVersionService appVersionService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody AppVersionDO appVersionDO) {
        logger.info(Arrays.asList("create", JSON.toJSONString(appVersionDO)).stream().collect(Collectors.joining("\u0001")));
        return appVersionService.create(appVersionDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody AppVersionDO appVersionDO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(appVersionDO)).stream().collect(Collectors.joining("\u0001")));
        return appVersionService.update(appVersionDO);
    }

    @GetMapping("/detail")
    public ResultBean<AppVersionVO> detail(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("detail", JSON.toJSONString(id)).stream().collect(Collectors.joining("\u0001")));
        return appVersionService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppVersionVO>> query(@RequestBody AppVersionQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
        return appVersionService.query(query);
    }

    @PostMapping(value = "/check", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<AppVersionVO.Update> checkUpdate(@RequestBody AppVersionDO appVersionDO) {
        logger.info(Arrays.asList("check", JSON.toJSONString(appVersionDO)).stream().collect(Collectors.joining("\u0001")));
        return appVersionService.checkUpdate(appVersionDO);
    }
}
