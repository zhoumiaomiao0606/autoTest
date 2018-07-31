package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VideoFaceLogDO;
import com.yunche.loan.domain.query.VideoFaceQuery;

import java.util.List;

public interface VideoFaceLogDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(VideoFaceLogDO record);

    int insertSelective(VideoFaceLogDO record);

    VideoFaceLogDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VideoFaceLogDO record);

    int updateByPrimaryKey(VideoFaceLogDO record);

    List<VideoFaceLogDO> query(VideoFaceQuery videoFaceQuery);

    /**
     * 最后一条 视频面签记录
     *
     * @param orderId
     * @return
     */
    VideoFaceLogDO lastVideoFaceLogByOrderId(Long orderId);
}