package com.yunche.loan.mapper;


import com.yunche.loan.domain.entity.BankFileListDO;
import com.yunche.loan.domain.entity.BankFileListRecordDO;
import com.yunche.loan.domain.entity.LoanRepayPlanDO;
import com.yunche.loan.domain.param.BankRepayParam;
import com.yunche.loan.domain.vo.InsuranceUrgeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BankRecordQueryDOMapper {


    BankRepayParam selectByIdCardOrRepayCard(@Param("idCard") String idCard, @Param("repayCard") String repayCard);

    List<LoanRepayPlanDO> selectRepayPlanListByOrderId(@Param("orderId") Long orderId);
    List<LoanRepayPlanDO>  selectOverdueRepayPlanList(@Param("orderId") Long orderId,
                                                      @Param("batchDate") Date batchDate, @Param("overdueTimes") Integer overdueTimes);

    List<BankFileListDO> selectBankImpRecord(@Param("fileName") String fileName,@Param("startDate") String startDate,
                                                  @Param("endDate") String endDate, @Param("fileType") String fileType);

    List<BankFileListRecordDO>selectBankRecordDetail(@Param("listId") Long listId,
                                                     @Param("userName") String userName,
                                                     @Param("idCard") String idCard,
                                                     @Param("isCustomer") Byte isCustomer);


    LoanRepayPlanDO selectRepayPlanByNper(@Param("orderId") Long orderId, @Param("nper") Integer nper);


    /**
     * 催保工作台列表
     * @return
     */
    List<InsuranceUrgeVO> selectInsuranceUrgeTaskList(@Param("taskStatus") Byte taskStatus);
}
