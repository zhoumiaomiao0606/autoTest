package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VideoFaceLogDO;

public interface VideoFaceLogDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(VideoFaceLogDO record);

    int insertSelective(VideoFaceLogDO record);

    VideoFaceLogDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VideoFaceLogDO record);

    int updateByPrimaryKey(VideoFaceLogDO record);
}