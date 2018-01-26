package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.EmployeeQuery;
import com.yunche.loan.domain.dataObj.EmployeeDO;
import com.yunche.loan.domain.param.EmployeeParam;
import com.yunche.loan.domain.viewObj.EmployeeVO;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.service.EmployeeService;
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
 * @date 2018/1/23
 */
@CrossOrigin
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody EmployeeParam employeeParam) {
        logger.info(Arrays.asList("create", JSON.toJSONString(employeeParam)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.create(employeeParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody EmployeeDO employeeDO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(employeeDO)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.update(employeeDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(id)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<EmployeeVO> getById(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("getById", JSON.toJSONString(id)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<EmployeeVO>> query(@RequestBody EmployeeQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.query(query);
    }

    /**
     * 员工-级联列表
     *
     * @return
     */
    @GetMapping("/list")
    public ResultBean<List<LevelVO>> listAll() {
        logger.info("list");
        return employeeService.listAll();
    }
}
