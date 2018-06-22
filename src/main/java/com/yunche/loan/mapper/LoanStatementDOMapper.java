package com.yunche.loan.mapper;

import com.yunche.loan.domain.param.TelephoneVerifyParam;
import com.yunche.loan.domain.vo.TelephoneVerifyNodeOrdersVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoanStatementDOMapper {


    List<TelephoneVerifyNodeOrdersVO> statisticsTelephoneVerifyNodeOrders(TelephoneVerifyParam telephoneVerifyParam);


}
