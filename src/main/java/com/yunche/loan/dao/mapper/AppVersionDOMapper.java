package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.AppVersionDO;
import com.yunche.loan.domain.queryObj.BaseQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AppVersionDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AppVersionDO record);

    int insertSelective(AppVersionDO record);

    AppVersionDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(AppVersionDO record);

    int updateByPrimaryKey(AppVersionDO record);

    void updateLatestVersion(AppVersionDO appVersionDO);

    int count(BaseQuery query);

    List<AppVersionDO> query(BaseQuery query);
}