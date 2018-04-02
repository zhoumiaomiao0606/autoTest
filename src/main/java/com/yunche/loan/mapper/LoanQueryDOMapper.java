package com.yunche.loan.mapper;

import com.yunche.loan.domain.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanQueryDOMapper {
    VehicleInformationVO selectVehicleInformation(Long orderId);

    public ApplyLicensePlateDepositInfoVO selectApplyLicensePlateDepositInfo(Long orderId);

    public BusinessReviewVO selectBusinessReview(Long orderId);

    public FinanceVO selectFinance(Long orderId);

    public MaterialVO selectMaterial(Long orderId);

    public List<InsuranceCustomerVO> selectInsuranceCustomer(Long orderId);

    public InsuranceCustomerVO selectInsuranceCustomerNormalizeInsuranceYear(Long orderId);

    public List<InsuranceRelevanceVO> selectInsuranceRelevance(Long insuranceInfoId);

    public FinancialSchemeVO selectFinancialScheme(Long orderId);

    public List<UniversalCustomerVO> selectUniversalCustomer(Long orderId);

    UniversalCustomerDetailVO selectUniversalCustomerDetail(Long customerId);

    public List<UniversalCustomerFileVO> selectUniversalCustomerFile(Long customerId);

    public List<UniversalMaterialRecordVO> selectUniversalMaterialRecord(Long orderId);

    public List<UniversalMaterialRecordVO> selectUniversalMaterialRecordByType(@Param("orderId") Long orderId, @Param("uploadType") Byte uploadType);

    CostCalculateInfoVO selectCostCalculateInfo(Long orderId);

    List<GpsVO> selectGpsByOrderId(Long orderId);

    BankLendRecordVO selectBankLendRecordDetail(Long orderId);

    Long  selectOrderIdByIDCard(String idCard);
}
