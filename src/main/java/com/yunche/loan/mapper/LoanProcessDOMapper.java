package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessDO;
import org.apache.ibatis.annotations.Select;

public interface LoanProcessDOMapper {

    int deleteByPrimaryKey(Long orderId);

    int insert(LoanProcessDO record);

    int insertSelective(LoanProcessDO record);

    LoanProcessDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(LoanProcessDO record);

    int updateByPrimaryKey(LoanProcessDO record);

    /**
     * 获取弃单字段
     *
     * @param orderId
     * @return
     */
    @Select("SELECT cancel_task_def_key FROM `loan_process`  WHERE `order_id` = #{orderId,jdbcType=BIGINT}")
    String getCancelTaskDefKey(Long orderId);
}