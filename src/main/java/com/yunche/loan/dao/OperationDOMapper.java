package com.yunche.loan.dao;

import com.yunche.loan.domain.query.AuthQuery;
import com.yunche.loan.domain.entity.OperationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OperationDO record);

    int insertSelective(OperationDO record);

    OperationDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(OperationDO record);

    int updateByPrimaryKey(OperationDO record);

    List<OperationDO> getAll(@Param("status") Byte status);

    List<Long> getAllIdList(@Param("status") Byte status);

    /**
     * 条件查询 - 获取所有 operation ID列表
     *
     * @param query
     * @return
     */
    List<Long> queryAllOperationIdList(AuthQuery query);
}