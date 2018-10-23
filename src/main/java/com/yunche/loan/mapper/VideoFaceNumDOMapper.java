package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VideoFaceNumDO;

public interface VideoFaceNumDOMapper {
    int deleteByPrimaryKey(Long order_id);

    int insert(VideoFaceNumDO record);

    int insertSelective(VideoFaceNumDO record);

    VideoFaceNumDO selectByPrimaryKey(Long order_id);

    int updateByPrimaryKeySelective(VideoFaceNumDO record);

    int updateByPrimaryKey(VideoFaceNumDO record);
}