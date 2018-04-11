package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoanBaseInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanBaseInfoDO record);

    int insertSelective(LoanBaseInfoDO record);

    LoanBaseInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanBaseInfoDO record);

    int updateByPrimaryKey(LoanBaseInfoDO record);

    @Select("SELECT * FROM `loan_base_info` WHERE `id` = (SELECT `loan_base_info_id`  FROM `loan_order` WHERE id = #{orderId})")
    LoanBaseInfoDO getByOrderId(Long orderId);
}