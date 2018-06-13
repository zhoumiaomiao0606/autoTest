package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VideoFaceRoomDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoFaceRoomMapper {
    int deleteByPrimaryKey(Long id);

    int insert(VideoFaceRoomDO record);

    int insertSelective(VideoFaceRoomDO record);

    VideoFaceRoomDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VideoFaceRoomDO record);

    int updateByPrimaryKey(VideoFaceRoomDO record);

    VideoFaceRoomDO getByBankId(Long bankId);
}