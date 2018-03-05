package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessOrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoanProcessOrderDOMapper {
    int deleteByPrimaryKey(String id);

    int insert(LoanProcessOrderDO record);

    int insertSelective(LoanProcessOrderDO record);

    LoanProcessOrderDO selectByPrimaryKey(@Param("id") String id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(LoanProcessOrderDO record);

    int updateByPrimaryKey(LoanProcessOrderDO record);

    /**
     * 获取主贷人ID            
     *
     * @param id
     * @return
     */
    Long getCustIdById(String id);

    /**
     * 根据流程实例ID获取业务单
     *
     * @param processInstId
     * @return
     */
    LoanProcessOrderDO getByProcessInstId(String processInstId);

    /**
     * 根据ID获取loanCarInfoId
     *
     * @param id
     * @return
     */
    Long getLoanCarInfoIdById(String id);

    /**
     * 根据ID获取loanFinancialPlanId
     *
     * @param id
     * @return
     */
    Long getLoanFinancialPlanIdById(String id);

    /**
     * 根据ID获取loanHomeVisitId
     *
     * @param id
     * @return
     */
    Long getLoanHomeVisitId(String id);
}