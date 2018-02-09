package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.InstProcessNodeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InstProcessNodeDOMapper {
    int deleteByPrimaryKey(Long nodeId);

    int insert(InstProcessNodeDO record);

    int insertSelective(InstProcessNodeDO record);

    InstProcessNodeDO selectByPrimaryKey(Long nodeId);

    List<InstProcessNodeDO> selectByOrderIdAndNodeCode(@Param("orderId") Long orderId, @Param("nodeCode") String nodeCode);

    List<InstProcessNodeDO> selectByOrderId(@Param("orderId") Long orderId);

    int updateByPrimaryKeySelective(InstProcessNodeDO record);

    int updateByPrimaryKey(InstProcessNodeDO record);
}