package com.yunche.loan.web.controller;

import com.yunche.loan.domain.vo.CascadeAreaVO;
import com.yunche.loan.domain.vo.BaseAreaVO;
import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.query.BaseAreaQuery;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.BaseAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/area")
public class BaseAreaController {

    @Autowired
    private BaseAreaService baseAreaService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody BaseAreaDO baseAreaDO) {
        return baseAreaService.create(baseAreaDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody BaseAreaDO baseAreaDO) {
        return baseAreaService.update(baseAreaDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long areaId) {
        return baseAreaService.delete(areaId);
    }

    @GetMapping("/getById")
    public ResultBean<BaseAreaVO> getById(@RequestParam("id") Long areaId) {
        return baseAreaService.getById(areaId);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<BaseAreaVO> query(@RequestBody BaseAreaQuery query) {
        return baseAreaService.query(query);
    }

    @GetMapping("/list")
    public ResultBean<List<CascadeAreaVO>> list() {
        return baseAreaService.list();
    }

    @GetMapping("/register")
    public ResultBean<List<CascadeAreaVO>> queryApplyLicensePlateArea(@RequestParam("orderId") Long orderId){

        return baseAreaService.getApplyLicensePlateArea(orderId);
    }
}
