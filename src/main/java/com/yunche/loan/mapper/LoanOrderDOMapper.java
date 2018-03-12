package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.query.AppLoanOrderQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 统计：征信查询未完成的业务单总数   -包含任务状态：【征信申请单、征信申请单审核、银行征信、社会征信】
     *
     * @return
     * @param query
     */
    long countCreditNotEnding(AppLoanOrderQuery query);

    /**
     * 征信查询未完成的业务单列表   -包含任务状态：【征信申请单、征信申请单审核、银行征信、社会征信】
     *
     * @return
     * @param query
     */
    List<LoanOrderDO> listCreditNotEnding(AppLoanOrderQuery query);
}