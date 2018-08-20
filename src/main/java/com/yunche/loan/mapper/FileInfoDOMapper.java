package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.FileInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FileInfoDO record);

    int insertSelective(FileInfoDO record);

    FileInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FileInfoDO record);

    int updateByPrimaryKey(FileInfoDO record);

    FileInfoDO selectByOrderId(@Param("orderid")Long orderid,@Param("bankRepayImpRecordId")Long bankRepayImpRecordId);
}