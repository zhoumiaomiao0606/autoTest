package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.MaterialDownHisDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MaterialDownHisDOMapper {
    int deleteByPrimaryKey(Long serialNo);

    int insert(MaterialDownHisDO record);

    int insertSelective(MaterialDownHisDO record);

    MaterialDownHisDO selectByPrimaryKey(Long serialNo);

    int updateByPrimaryKeySelective(MaterialDownHisDO record);

    int updateByPrimaryKey(MaterialDownHisDO record);

    List<MaterialDownHisDO> listByStatus(Byte status);
}