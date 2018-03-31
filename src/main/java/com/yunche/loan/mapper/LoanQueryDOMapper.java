package com.yunche.loan.mapper;

import com.yunche.loan.domain.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoanQueryDOMapper {

    public ApplyLicensePlateDepositInfoVO selectApplyLicensePlateDepositInfo(Long orderId);

    public ApplyLicensePlateRecordVO selectApplyLicensePlateRecord(Long orderId);

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

    CostCalculateInfoVO selectCostCalculateInfo(Long orderId);

    List<GpsVO> selectGpsByOrderId(Long orderId);

    /**
     * 提车资料查询
     */
    VehicleInfoVO selectVehicleInformation(Long orderId);

    /**
     * 银行放款记录明细查询
     */
    BankLendRecordVO selectBankLendRecordDetail(Long orderId);

    /**
     *
     * @param idCard 身份证号
     * @return
     */
   Long  selectOrderIdByIDCard(String idCard);
}
