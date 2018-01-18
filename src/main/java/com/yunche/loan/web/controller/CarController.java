package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.valueObj.CarThreeLevelVO;
import com.yunche.loan.domain.valueObj.CarTwoLevelVO;
import com.yunche.loan.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@CrossOrigin
@RestController
@RequestMapping("/car")
public class CarController {

    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    @Autowired
    private CarService carService;

    @GetMapping("/import")
    public ResultBean<Void> importCar() {
        logger.info("import");
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
        logger.info("fillModel");
        return carService.fillModel();
    }

    /**
     * 统计API服务车型总量
     *
     * @return
     */
    @GetMapping("/count")
    public ResultBean<String> count() {
        logger.info("count");
        return carService.count();
    }

    /**
     * 三级联动关系  -All
     *
     * @return
     */
    @GetMapping("/list/threeLevel")
    public ResultBean<CarThreeLevelVO> listThreeLevel() {
        logger.info("/listThreeLevel");
        return carService.listAll();
    }

    /**
     * 两级联动 -All
     *
     * @return
     */
    @GetMapping("/list/twoLevel")
    public ResultBean<CarTwoLevelVO> listTwoLevel() {
        logger.info("/listTwoLevel");
        return carService.listTwoLevel();
    }

    /**
     * 三级联动关系   -ONE
     * <p>
     * 单个品牌下
     *
     * @param brandId
     * @return
     */
    @GetMapping("/list")
    public ResultBean<CarThreeLevelVO.CarOneBrandThreeLevelVO> list(@RequestParam("brandId") Long brandId) {
        logger.info(Arrays.asList("list", brandId.toString()).stream().collect(Collectors.joining("-")));
        return carService.list(brandId);
    }

}


