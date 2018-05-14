package com.yunche.loan;


import com.alibaba.fastjson.JSON;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.VehicleInformationDOMapper;
import com.yunche.loan.config.tree.TreeFactory;
import com.yunche.loan.config.tree.TreeNode;
import com.yunche.loan.service.CollectionService;

import javax.annotation.Resource;
import java.util.List;

public class Test extends BaseTest {
    @Resource
    private CollectionService collectionService;
    @org.junit.Test
    public void test(){

        collectionService.autoDistribution();
    }



}
