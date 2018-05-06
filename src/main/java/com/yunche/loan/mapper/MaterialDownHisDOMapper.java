package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.MaterialDownHisDO;
import com.yunche.loan.domain.entity.MaterialDownHisDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface MaterialDownHisDOMapper {
    int deleteByPrimaryKey(MaterialDownHisDOKey key);


    int insert(MaterialDownHisDO record);

    int insertSelective(MaterialDownHisDO record);

    MaterialDownHisDO selectByPrimaryKey(MaterialDownHisDOKey key);

    int updateByPrimaryKeySelective(MaterialDownHisDO record);

    int updateByPrimaryKey(MaterialDownHisDO record);

    List<MaterialDownHisDO> listAll();
}