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

    public InsuranceCustomerVO selectInsuranceCustomer(Long orderId);

    public List<InsuranceRelevanceVO> selectInsuranceRelevance(Long insuranceInfoId);

    public FinancialSchemeVO selectFinancialScheme(Long orderId);

    public List<UniversalCustomerVO> selectUniversalCustomer(Long orderId);

    public List<UniversalCustomerFileVO> selectUniversalCustomerFile(Long customerId);

    public List<UniversalMaterialRecordVO> selectUniversalMaterialRecord(Long orderId);

}
