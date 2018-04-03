package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanProcessLogDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanProcessLogDO record);

    int insertSelective(LoanProcessLogDO record);

    LoanProcessLogDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanProcessLogDO record);

    int updateByPrimaryKey(LoanProcessLogDO record);

    /**
     * 查询指定订单-指定任务的最后一条操作记录
     *
     * @param orderId
     * @param taskDefinitionKey
     * @return
     */
    LoanProcessLogDO lastLogByOrderIdAndTaskDefinitionKey(@Param("orderId") Long orderId, @Param("taskDefinitionKey") String taskDefinitionKey);

    /**
     * 获取指定订单的操作记录列表
     *
     * @param orderId
     * @param limit
     * @return
     */
    List<LoanProcessLogDO> listByOrderId(@Param("orderId") Long orderId, @Param("limit") Integer limit);
}