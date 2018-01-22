package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.domain.QueryObj.PaddingCompanyQuery;
import com.yunche.loan.domain.viewObj.PaddingCompanyVO;
import com.yunche.loan.domain.dataObj.PaddingCompanyDO;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.PaddingCompanyService;
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
 * @date 2018/1/15
 */
@CrossOrigin
@RestController
@RequestMapping("/padding")
public class PaddingCompanyController {

    private static final Logger logger = LoggerFactory.getLogger(PaddingCompanyController.class);

    @Autowired
    private PaddingCompanyService paddingCompanyService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody PaddingCompanyDO paddingCompanyDO) {
        logger.info(Arrays.asList("create", JSON.toJSONString(paddingCompanyDO)).stream().collect(Collectors.joining("-")));
        return paddingCompanyService.create(paddingCompanyDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody PaddingCompanyDO paddingCompanyDO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(paddingCompanyDO)).stream().collect(Collectors.joining("-")));
        return paddingCompanyService.update(paddingCompanyDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(id)).stream().collect(Collectors.joining("-")));
        return paddingCompanyService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<PaddingCompanyVO> getById(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("getById", JSON.toJSONString(id)).stream().collect(Collectors.joining("-")));
        return paddingCompanyService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<PaddingCompanyVO>> query(@RequestBody PaddingCompanyQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("-")));
        return paddingCompanyService.query(query);
    }
}
