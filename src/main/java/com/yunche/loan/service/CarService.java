package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface CarService {

    ResultBean<Void> importCar();

    ResultBean<Void> fillModel();

    ResultBean<String> count();
}
