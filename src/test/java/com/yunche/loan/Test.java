package com.yunche.loan;


import com.yunche.loan.mapper.VehicleInformationDOMapper;

import javax.annotation.Resource;

public class Test extends BaseTest {
    @Resource
    private VehicleInformationDOMapper vehicleInformationDOMapper;
    @org.junit.Test
    public void test(){
        vehicleInformationDOMapper.insertSelective(null);
    }

}
