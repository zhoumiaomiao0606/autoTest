package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.FlowOperationMsgDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FlowOperationMsgDOMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(FlowOperationMsgDO record);

    FlowOperationMsgDO selectByPrimaryKey(Long id);

    List<FlowOperationMsgDO> selectByEmployeeId(Long employeeId);

    int updateByPrimaryKeySelective(FlowOperationMsgDO record);


}