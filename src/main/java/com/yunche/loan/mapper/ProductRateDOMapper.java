package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ProductRateDO;
import com.yunche.loan.domain.entity.ProductRateDOKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductRateDOMapper {
    int deleteByPrimaryKey(ProductRateDOKey key);

    int insert(ProductRateDO record);

    int insertSelective(ProductRateDO record);

    ProductRateDO selectByPrimaryKey(ProductRateDOKey key);

    int updateByPrimaryKeySelective(ProductRateDO record);

    int updateByPrimaryKey(ProductRateDO record);
}