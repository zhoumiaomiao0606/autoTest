package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.DataManagementInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DataManagementInfoDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(DataManagementInfoDO record);

    int insertSelective(DataManagementInfoDO record);

    DataManagementInfoDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(DataManagementInfoDO record);

    int updateByPrimaryKey(DataManagementInfoDO record);

    List<String> selectOrderByIdCard(@Param("idCard")String idCard);
}