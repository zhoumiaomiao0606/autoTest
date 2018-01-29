package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.AuthDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AuthDO record);

    int insertSelective(AuthDO record);

    AuthDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AuthDO record);

    int updateByPrimaryKey(AuthDO record);
}