package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanOrderDO;

import java.util.List;

public interface LoanOrderDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanOrderDO record);

    int insertSelective(LoanOrderDO record);

    LoanOrderDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanOrderDO record);

    int updateByPrimaryKey(LoanOrderDO record);

    List<Long> selectRelevanceLoanOrderIdByCustomerId(Long customerId);

    /**
     * 获取主贷人ID            
     *
     * @param id
     * @return
     */
    Long getCustIdById(Long id);

    Long getVehicleInformationIdById(Long id);

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

    LoanOrderDO selectByCustomerId(Long customerId);
}