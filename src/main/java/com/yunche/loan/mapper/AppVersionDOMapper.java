package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.AppVersionDO;
import com.yunche.loan.domain.query.BaseQuery;
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