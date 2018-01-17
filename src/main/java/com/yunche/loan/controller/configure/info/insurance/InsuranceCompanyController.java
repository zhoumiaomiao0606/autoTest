package com.yunche.loan.controller.configure.info.insurance;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.vo.configure.info.insurance.InsuranceCompanyVO;
import com.yunche.loan.obj.configure.info.insurance.InsuranceCompanyDO;
import com.yunche.loan.query.configure.info.insurance.InsuranceCompanyQuery;
import com.yunche.loan.result.ResultBean;
import com.yunche.loan.service.configure.info.insurance.InsuranceCompanyService;
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
@RequestMapping("/insurance")
public class InsuranceCompanyController {

    private static final Logger logger = LoggerFactory.getLogger(InsuranceCompanyController.class);

    @Autowired
    private InsuranceCompanyService insuranceCompanyService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> create(@RequestBody InsuranceCompanyDO insuranceCompanyDO) {
        logger.info(Arrays.asList("create", JSON.toJSONString(insuranceCompanyDO)).stream().collect(Collectors.joining("-")));
        return insuranceCompanyService.create(insuranceCompanyDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> update(@RequestBody InsuranceCompanyDO insuranceCompanyDO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(insuranceCompanyDO)).stream().collect(Collectors.joining("-")));
        return insuranceCompanyService.update(insuranceCompanyDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Integer id) {
        logger.info(Arrays.asList("delete", id.toString()).stream().collect(Collectors.joining("-")));
        return insuranceCompanyService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<InsuranceCompanyVO> getById(@RequestParam("id") Integer id) {
        logger.info(Arrays.asList("getById", id.toString()).stream().collect(Collectors.joining("-")));
        return insuranceCompanyService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<List<InsuranceCompanyVO>> query(@RequestBody InsuranceCompanyQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("-")));
        return insuranceCompanyService.query(query);
    }
}
