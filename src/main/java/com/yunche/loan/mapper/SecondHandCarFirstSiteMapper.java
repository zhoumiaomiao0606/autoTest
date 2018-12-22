package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.SecondHandCarFirstSite;

public interface SecondHandCarFirstSiteMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SecondHandCarFirstSite record);

    int insertSelective(SecondHandCarFirstSite record);

    SecondHandCarFirstSite selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SecondHandCarFirstSite record);

    int updateByPrimaryKey(SecondHandCarFirstSite record);
}