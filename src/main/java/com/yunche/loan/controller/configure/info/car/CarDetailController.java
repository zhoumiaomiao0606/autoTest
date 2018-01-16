package com.yunche.loan.controller.configure.info.car;

import com.yunche.loan.service.configure.info.car.CarDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@CrossOrigin
@RestController
@RequestMapping("/car/series")
public class CarDetailController {

    private static final Logger logger = LoggerFactory.getLogger(CarDetailController.class);

    @Autowired
    private CarDetailService carDetailService;



}
