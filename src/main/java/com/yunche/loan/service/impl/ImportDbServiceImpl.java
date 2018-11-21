package com.yunche.loan.service.impl;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.mapper.CarBrandDOMapper;
import com.yunche.loan.mapper.CarDetailDOMapper;
import com.yunche.loan.mapper.CarModelDOMapper;
import com.yunche.loan.service.ImportDbService;
import org.springframework.beans.factory.annotation.Autowired;

public class ImportDbServiceImpl implements ImportDbService
{
    @Autowired
    private CarBrandDOMapper carBrandDOMapper;

    @Autowired
    private CarModelDOMapper carModelDOMapper;

    @Autowired
    private CarDetailDOMapper carDetailDOMapper;



    @Override
    public ResultBean db()
    {
        return null;
    }
}
