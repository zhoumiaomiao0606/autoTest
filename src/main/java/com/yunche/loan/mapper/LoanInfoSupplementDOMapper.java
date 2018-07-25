package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanInfoSupplementDO;
import org.apache.ibatis.annotations.Select;

public interface LoanInfoSupplementDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanInfoSupplementDO record);

    int insertSelective(LoanInfoSupplementDO record);

    LoanInfoSupplementDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanInfoSupplementDO record);

    int updateByPrimaryKey(LoanInfoSupplementDO record);

    @Select("SELECT COUNT(id)  FROM `loan_info_supplement` WHERE `order_id` = #{orderId,jdbcType=BIGINT}")
    int countByOrderId(Long orderId);
}