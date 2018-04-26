package com.yunche.loan;


import com.alibaba.fastjson.JSON;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.VehicleInformationDOMapper;
import com.yunche.loan.config.tree.TreeFactory;
import com.yunche.loan.config.tree.TreeNode;

import javax.annotation.Resource;
import java.util.List;

public class Test extends BaseTest {
    @Resource
    private VehicleInformationDOMapper vehicleInformationDOMapper;
    @org.junit.Test
    public void test(){
        vehicleInformationDOMapper.insertSelective(null);
    }



}
