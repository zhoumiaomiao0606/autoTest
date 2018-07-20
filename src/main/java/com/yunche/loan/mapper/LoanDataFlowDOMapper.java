package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanDataFlowDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoanDataFlowDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanDataFlowDO record);

    int insertSelective(LoanDataFlowDO record);

    LoanDataFlowDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanDataFlowDO record);

    int updateByPrimaryKey(LoanDataFlowDO record);

    /**
     * 根据orderId、type获取最后一条记录
     *
     * @param orderId
     * @param type
     * @return
     */
    LoanDataFlowDO getLastByOrderIdAndType(@Param("orderId") Long orderId, @Param("type") Byte type);

    /**
     * 批量插入
     *
     * @param loanDataFlowDOList
     * @return
     */
    int batchInsert(List<LoanDataFlowDO> loanDataFlowDOList);
}