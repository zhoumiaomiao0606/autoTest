package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.MenuDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MenuDO record);

    int insertSelective(MenuDO record);

    MenuDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(MenuDO record);

    int updateByPrimaryKey(MenuDO record);

    List<MenuDO> getAll(@Param("status") Byte status);
}