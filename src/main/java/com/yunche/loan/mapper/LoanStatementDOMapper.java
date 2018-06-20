package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanTelephoneVerifyDO;
import com.yunche.loan.domain.vo.TelephoneVerifyNodeOrdersVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanStatementDOMapper {


    List<TelephoneVerifyNodeOrdersVO> statisticsTelephoneVerifyNodeOrders(@Param("startDate") String startDate, @Param("endDate") String endDate);


}
