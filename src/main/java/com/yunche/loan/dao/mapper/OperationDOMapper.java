package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.OperationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperationDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OperationDO record);

    int insertSelective(OperationDO record);

    OperationDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OperationDO record);

    int updateByPrimaryKey(OperationDO record);

    List<OperationDO> getAll(Byte validStatus);
}