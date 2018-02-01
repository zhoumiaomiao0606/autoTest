package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.viewObj.CarThreeLevelVO;
import com.yunche.loan.domain.viewObj.CarTwoLevelVO;

import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface CarService {

    ResultBean<Void> importCar();

    ResultBean<Void> fillModel();

    ResultBean<Map<String,Integer>> count();

    /**
     * 三级联动关系   -All
     *
     * @return
     */
    ResultBean<CarThreeLevelVO> listAll();

    /**
     * 三级联动关系   -单个品牌下
     *
     * @param brandId
     * @return
     */
    ResultBean<CarThreeLevelVO.CarOneBrandThreeLevelVO> list(Long brandId);

    /**
     * 两级联动
     *
     * @return
     */
    ResultBean<CarTwoLevelVO> listTwoLevel();
}
