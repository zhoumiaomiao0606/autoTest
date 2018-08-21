package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.FileInfoDO;

public interface FileInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FileInfoDO record);

    int insertSelective(FileInfoDO record);

    FileInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FileInfoDO record);

    int updateByPrimaryKeyWithBLOBs(FileInfoDO record);

    int updateByPrimaryKey(FileInfoDO record);
}