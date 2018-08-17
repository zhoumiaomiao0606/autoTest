package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganOverdueDO;

public interface ZhonganOverdueDOMapper {
    int insert(ZhonganOverdueDO record);

    int insertSelective(ZhonganOverdueDO record);

    List<ZhongAnOverDueDO> selectById(@Param("id")Long id);
}