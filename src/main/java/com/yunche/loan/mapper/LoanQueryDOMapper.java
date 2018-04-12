package com.yunche.loan.mapper;

import com.yunche.loan.domain.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanQueryDOMapper {
    VehicleInformationVO selectVehicleInformation(Long orderId);

    public ApplyLicensePlateDepositInfoVO selectApplyLicensePlateDepositInfo(Long orderId);

    public UniversalInfoVO selectUniversalInfo(Long orderId);

    public UniversalApprovalInfo selectUniversalApprovalInfo(@Param("taskDefinitionKey") String taskDefinitionKey,@Param("orderId") Long orderId);

    public UniversalLoanInfoVO selectUniversalLoanInfo(Long orderId);

    public List<Long> selectUniversalRelevanceOrderId(Long orderId);

    public UniversalCarInfoVO selectUniversalCarInfo(Long orderId);

    public List<UniversalRelationCustomerVO> selectUniversalRelationCustomer(Long orderId);

    public List<Long> selectUniversalRelevanceOrderIdByCustomerId(Long customerId);

    public UniversalRemitDetails selectUniversalRemitDetails(Long orderId);

    public UniversalCostDetailsVO selectUniversalCostDetails(Long orderId);

    public List<UniversalCreditInfoVO> selectUniversalCreditInfo(Long orderId);

    public UniversalHomeVisitInfoVO selectUniversalHomeVisitInfo(Long orderId);

    public UniversalSupplementInfoVO selectUniversalSupplementInfo(Long orderId);

    public List<UniversalCustomerVO> selectUniversalCustomer(Long orderId);

    public UniversalCustomerDetailVO selectUniversalCustomerDetail(@Param("orderId") Long orderId,@Param("customerId") Long customerId);

    public List<UniversalCustomerFileVO> selectUniversalCustomerFile(Long customerId);

    public List<UniversalMaterialRecordVO> selectUniversalMaterialRecord(Long orderId);

    public List<UniversalMaterialRecordVO> selectUniversalMaterialRecordByType(@Param("orderId") Long orderId, @Param("uploadType") Byte uploadType);

    public Long selectOrderIdbyPrincipalCustId(Long customerId);
    //=======================================================================

    public List<InsuranceCustomerVO> selectInsuranceCustomer(Long orderId);

    public InsuranceCustomerVO selectInsuranceCustomerNormalizeInsuranceYear(Long orderId);

    public List<InsuranceRelevanceVO> selectInsuranceRelevance(Long insuranceInfoId);

    public FinancialSchemeVO selectFinancialScheme(Long orderId);

    CostCalculateInfoVO selectCostCalculateInfo(Long orderId);

    List<GpsVO> selectGpsByOrderId(Long orderId);

    BankLendRecordVO selectBankLendRecordDetail(Long orderId);

    Long  selectOrderIdByIDCard(String idCard);

    BankCardRecordVO selectBankCardRecordDetail(Long orderId);
}
