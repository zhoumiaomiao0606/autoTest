package com.yunche.loan.controller.configure.info.car;

import com.yunche.loan.service.configure.info.car.CarBrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@CrossOrigin
@RestController
@RequestMapping("/car/brand")
public class CarBrandController {

    private static final Logger logger = LoggerFactory.getLogger(CarBrandController.class);

    @Autowired
    private CarBrandService carBrandService;


}
