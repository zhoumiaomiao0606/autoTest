package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankDO;
import com.yunche.loan.domain.query.BankQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BankDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BankDO record);

    int insertSelective(BankDO record);

    BankDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankDO record);

    int updateByPrimaryKey(BankDO record);

    List<BankDO> listAll(@Param("status") Byte status);

    /**
     * 分页条件查询
     *
     * @param query
     * @return
     */
    List<BankDO> query(BankQuery query);

    int count(BankQuery query);

    Long selectIdByName(String name);
}