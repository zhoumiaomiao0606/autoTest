package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ConfVideoFaceMachineDO;
import com.yunche.loan.domain.query.ConfVideoFaceMachineQuery;

import java.util.List;

public interface ConfVideoFaceMachineDOMapper {

    int deleteByPrimaryKey(ConfVideoFaceMachineDO key);

    int insert(ConfVideoFaceMachineDO record);

    int insertSelective(ConfVideoFaceMachineDO record);

    ConfVideoFaceMachineDO selectByPrimaryKey(ConfVideoFaceMachineDO key);

    int updateByPrimaryKeySelective(ConfVideoFaceMachineDO record);

    int updateByPrimaryKey(ConfVideoFaceMachineDO record);

    List<ConfVideoFaceMachineDO> query(ConfVideoFaceMachineQuery query);
}