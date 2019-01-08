package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ConfVideoFaceArtificialDO;

public interface ConfVideoFaceArtificialDOMapper {

    int deleteByPrimaryKey(Long bankId);

    int insert(ConfVideoFaceArtificialDO record);

    int insertSelective(ConfVideoFaceArtificialDO record);

    ConfVideoFaceArtificialDO selectByPrimaryKey(Long bankId);

    int updateByPrimaryKeySelective(ConfVideoFaceArtificialDO record);

    int updateByPrimaryKey(ConfVideoFaceArtificialDO record);
}