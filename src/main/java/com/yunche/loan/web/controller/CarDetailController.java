package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.CarDetailQuery;
import com.yunche.loan.domain.entity.CarDetailDO;
import com.yunche.loan.domain.vo.CarDetailVO;
import com.yunche.loan.service.CarDetailService;
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
@RequestMapping("/api/v1/car/detail")
public class CarDetailController {

    @Autowired
    private CarDetailService carDetailService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody CarDetailDO carDetailDO) {
        return carDetailService.create(carDetailDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody CarDetailDO carDetailDO) {
        return carDetailService.update(carDetailDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        return carDetailService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<CarDetailVO> getById(@RequestParam("id") Long id) {
        return carDetailService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<CarDetailVO>> query(@RequestBody CarDetailQuery query) {
        return carDetailService.query(query);
    }
}
