package com.yunche.loan.controller.configure.info.car;

import com.yunche.loan.result.ResultBean;
import com.yunche.loan.service.configure.info.car.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}


