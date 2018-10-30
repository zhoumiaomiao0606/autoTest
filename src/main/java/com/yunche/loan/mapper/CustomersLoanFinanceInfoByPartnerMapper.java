package com.yunche.loan.mapper;

import com.yunche.loan.domain.param.CustomerInfoByCustomerNameParam;
import com.yunche.loan.domain.param.FSysRebateParam;
import com.yunche.loan.domain.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomersLoanFinanceInfoByPartnerMapper
{


    List<BadBalanceByPartnerVO> selectBadBalance(Long partnerId);


    List<OverdueBalanceByPartnerVO> selectOverdueBalance(Long partnerId);


    List<InGuaranteeBalanceByPartnerVO> selectInGuaranteeBalance(Long partnerId);


    List<LoanBalanceByPartnerVO> selectLoanBalance(Long partnerId);

    List<OrderByCustomerIdVO> getOrderByCustomerId(Long customerId);

    List<CustomerInfoForFinanceSys> getCustomerInfoByCustomerName(CustomerInfoByCustomerNameParam customerInfoByCustomerNameParam);

    List<RefundOrderInfoByPartnerVO> selectRefundOrderInfoByPartner(Long partnerId);

    FSysCompensationVO selectCompensationInfoByPartner(Long partnerId);

    List<FSysCompensationVO> listCompensationInfoByPartner(Long partnerId);

    List<FSysRebateVO> rebateDetailsList(FSysRebateParam param);

    List<FSysRebateVO> generateCurrRebateRecord();

    List<FSysRebateDetailVO> rebateDetail(FSysRebateParam param);

    FSysRebateVO rebateDetailsefresh(FSysRebateParam param);

    OrderByCustomerIdVO getOrderByOrderId(Long orderId);
}
