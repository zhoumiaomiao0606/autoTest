package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganOverdueDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ZhonganOverdueDOMapper {
    int insert(ZhonganOverdueDO record);

    int insertSelective(ZhonganOverdueDO record);

    List<ZhonganOverdueDO> selectById(@Param("id") Long id);
}