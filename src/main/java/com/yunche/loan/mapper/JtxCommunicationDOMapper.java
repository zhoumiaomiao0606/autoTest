package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.JtxCommunicationDO;

public interface JtxCommunicationDOMapper {

    int deleteByPrimaryKey(String jtxId);

    int insert(JtxCommunicationDO record);

    int insertSelective(JtxCommunicationDO record);

    JtxCommunicationDO selectByPrimaryKey(String jtxId);

    int updateByPrimaryKeySelective(JtxCommunicationDO record);

    int updateByPrimaryKey(JtxCommunicationDO record);
}