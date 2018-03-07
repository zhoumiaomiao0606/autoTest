package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanOrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoanOrderDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanOrderDO record);

    int insertSelective(LoanOrderDO record);

    LoanOrderDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(LoanOrderDO record);

    int updateByPrimaryKey(LoanOrderDO record);

    /**
     * 获取主贷人ID            
     *
     * @param id
     * @return
     */
    Long getCustIdById(Long id);

    /**
     * 根据流程实例ID获取业务单
     *
     * @param processInstId
     * @return
     */
    LoanOrderDO getByProcessInstId(String processInstId);

    /**
     * 根据ID获取loanCarInfoId
     *
     * @param id
     * @return
     */
    Long getLoanCarInfoIdById(Long id);

    /**
     * 根据ID获取loanFinancialPlanId
     *
     * @param id
     * @return
     */
    Long getLoanFinancialPlanIdById(Long id);

    /**
     * 根据ID获取loanHomeVisitId
     *
     * @param id
     * @return
     */
    Long getLoanHomeVisitId(Long id);
}