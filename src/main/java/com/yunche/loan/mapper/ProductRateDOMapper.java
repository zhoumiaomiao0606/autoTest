package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ProductRateDO;
import com.yunche.loan.domain.entity.ProductRateDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductRateDOMapper {
    int deleteByPrimaryKey(ProductRateDOKey key);

    int insert(ProductRateDO record);

    int insertSelective(ProductRateDO record);

    ProductRateDO selectByPrimaryKey(ProductRateDOKey key);

    int updateByPrimaryKeySelective(ProductRateDO record);

    int updateByPrimaryKey(ProductRateDO record);

    int deleteByProdId(Long productId);

    List<ProductRateDO> selectByProdId(Long productId);
}