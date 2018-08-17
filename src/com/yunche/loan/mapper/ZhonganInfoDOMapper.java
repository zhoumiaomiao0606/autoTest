package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganInfoDO;

public interface ZhonganInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ZhonganInfoDO record);

    int insertSelective(ZhonganInfoDO record);

    ZhonganInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ZhonganInfoDO record);

    int updateByPrimaryKey(ZhonganInfoDO record);

    List<ZhonganInfoDO> selectByOrderId(@Param("orderid")Long orderid,@Param("customername")String customername);
}