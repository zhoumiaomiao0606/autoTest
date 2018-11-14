package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.JtxReturnFileDO;

public interface JtxReturnFileDOMapper {
    int insert(JtxReturnFileDO record);

    int insertSelective(JtxReturnFileDO record);
}