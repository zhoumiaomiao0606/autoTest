package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.CarModelQuery;
import com.yunche.loan.domain.entity.CarModelDO;
import com.yunche.loan.domain.vo.CarModelVO;
import com.yunche.loan.service.CarModelService;
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
@RequestMapping("/car/model")
public class CarModelController {

    @Autowired
    private CarModelService carModelService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody CarModelDO carModelDO) {
        return carModelService.create(carModelDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody CarModelDO carModelDO) {
        return carModelService.update(carModelDO);
    }

    /**
     * 逻辑删除
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        return carModelService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<CarModelVO> getById(@RequestParam("id") Long id) {
        return carModelService.getById(id);
    }

    /**
     * 分页查询
     * <p>
     * 品牌ID必传
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<CarModelVO>> query(@RequestBody CarModelQuery query) {
        return carModelService.query(query);
    }

}
