package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.CarBrandDO;
import com.yunche.loan.domain.vo.CarBrandVO;
import com.yunche.loan.service.CarBrandService;
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
@RequestMapping("/car/brand")
public class CarBrandController {

    @Autowired
    private CarBrandService carBrandService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody CarBrandDO carBrandDO) {
        return carBrandService.create(carBrandDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody CarBrandDO carBrandDO) {
        return carBrandService.update(carBrandDO);
    }

    /**
     * 逻辑删除
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        return carBrandService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<CarBrandVO> getById(@RequestParam("id") Long id) {
        return carBrandService.getById(id);
    }

    /**
     * 获取所有品牌
     *
     * @return
     */
    @GetMapping("/list")
    public ResultBean<List<CarBrandVO>> listAll() {
        return carBrandService.listAll();
    }

}
