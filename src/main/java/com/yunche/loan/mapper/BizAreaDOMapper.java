package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.BizAreaQuery;
import com.yunche.loan.domain.entity.BizAreaDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BizAreaDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BizAreaDO record);

    int insertSelective(BizAreaDO record);

    BizAreaDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(BizAreaDO record);

    int updateByPrimaryKeyWithBLOBs(BizAreaDO record);

    int updateByPrimaryKey(BizAreaDO record);

    List<String> getAllName(@Param("status") Byte status);

    int count(BizAreaQuery query);

    List<BizAreaDO> query(BizAreaQuery query);

    List<BizAreaDO> getByParentId(@Param("parentId") Long parentId, @Param("status") Byte status);

    List<BizAreaDO> getAll(@Param("status") Byte status);
}