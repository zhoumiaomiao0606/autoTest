package com.yunche.loan.mapper;


import com.yunche.loan.domain.entity.BankRepayImpRecordDO;
import com.yunche.loan.domain.entity.LoanRepayPlanDO;
import com.yunche.loan.domain.param.BankRepayParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BankRepayQueryDOMapper {


    BankRepayParam selectByIdCardOrRepayCard(@Param("idCard") String idCard, @Param("repayCard")String repayCard);

    List<LoanRepayPlanDO> selectRepayPlanListByOrderId(@Param("orderId") Long orderId);
    List<LoanRepayPlanDO>  selectOverdueRepayPlanList(@Param("orderId") Long orderId,
                                                      @Param("batchDate")Date batchDate, @Param("overdueTimes") Integer overdueTimes);

    List<BankRepayImpRecordDO> selectBankRepayImpRecord();
}
