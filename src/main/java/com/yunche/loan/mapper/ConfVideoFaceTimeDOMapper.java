package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ConfVideoFaceTimeDO;
import org.apache.ibatis.annotations.Delete;

public interface ConfVideoFaceTimeDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ConfVideoFaceTimeDO record);

    int insertSelective(ConfVideoFaceTimeDO record);

    ConfVideoFaceTimeDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ConfVideoFaceTimeDO record);

    int updateByPrimaryKey(ConfVideoFaceTimeDO record);

    @Delete("DELETE FROM `conf_video_face_time`")
    void deleteAll();
}