package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.CarThreeLevelVO;
import com.yunche.loan.domain.vo.CarCascadeVO;
import com.yunche.loan.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@CrossOrigin
@RestController
@RequestMapping("/car")
public class CarController {

    @Autowired
    private CarService carService;


    @GetMapping("/import")
    public ResultBean<Void> importCar() {
        return carService.importCar();
    }

    /**
     * car_model表补偿任务
     * <p>
     * price：  取car_detail的两极值          格式：10-15万
     * seatNum：取car_detail的所有座位数      格式：5/7/9
     *
     * @return
     */
    @GetMapping("/fillModel")
    public ResultBean<Void> fillModel() {
        return carService.fillModel();
    }

    /**
     * 统计API服务车型总量
     *
     * @return
     */
    @GetMapping("/count")
    public ResultBean<Map<String, Integer>> count() {
        return carService.count();
    }

    /**
     * 三级联动关系  -All
     * <p>
     * 品牌-车系-车型
     *
     * @return
     */
    @GetMapping("/list/allBrand")
    public ResultBean<CarThreeLevelVO> listThreeLevel() {
        return carService.listAll();
    }

    /**
     * 两级联动 -All
     * <p>
     * 品牌-车系
     *
     * @return
     */
    @GetMapping("/list")
    public ResultBean<CarCascadeVO> listTwoLevel() {
        return carService.listTwoLevel();
    }

    /**
     * 三级联动关系   -ONE
     * <p>
     * 单个品牌下
     * <p>
     * 品牌-车系-车型
     *
     * @param brandId
     * @return
     */
    @GetMapping("/list/brand")
    public ResultBean<CarThreeLevelVO.CarOneBrandThreeLevelVO> list(@RequestParam("brandId") Long brandId) {
        return carService.list(brandId);
    }

}


