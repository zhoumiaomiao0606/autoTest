package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.DictMapDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DictMapDOMapper {
    int insert(DictMapDO record);

    int insertSelective(DictMapDO record);

    List<DictMapDO> getAll();
}