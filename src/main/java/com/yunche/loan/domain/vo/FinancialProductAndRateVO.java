package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.FinancialProductDO;
import com.yunche.loan.domain.entity.ProductRateDO;
import lombok.Data;

import java.util.List;

@Data
public class FinancialProductAndRateVO {

    //金融产品基本信息
    private FinancialProductVO financialProductVO;

    //产品费率列表
    private List<ProductRateDO> productRateDOs;

}
