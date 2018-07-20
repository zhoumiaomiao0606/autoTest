package com.yunche.loan.mapper;

import com.yunche.loan.domain.vo.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface LoanQueryDOMapper {

    List<BankInterFaceSerialOrderStatusVO> selectBankInterFaceSerialOrderStatusByOrderId(Long orderId);

    ApplyDiviGeneralInfoVO selectApplyDiviGeneralInfo(Long orderId);

    String selectLastBankInterfaceSerialStatusByTransCode(@Param("customerId") Long customerId,@Param("transCode") String transCode);

    String selectLastBankInterfaceSerialNoteByTransCode(@Param("customerId") Long customerId,@Param("transCode") String transCode);

    UniversalMaterialRecordVO  getUniversalCustomerFilesByType(@Param("customerId") Long customerId,@Param("type") Byte type);

    UniversalBankInterfaceSerialVO selectUniversalLatestBankInterfaceSerial(@Param("customerId") Long customerId,@Param("transCode") String transCode);

    String selectTelephoneVerifyLevel(@Param("loginUserId") Long loginUserId );

    boolean checkCollectionUserRole(@Param("loginUserId") Long loginUserId);

    List<UniversalCustomerOrderVO> selectUniversalModifyCustomerOrder(@Param("employeeId") Long employeeId, @Param("name") String name);

    List<UniversalCustomerOrderVO> selectUniversalRefundCustomerOrder(@Param("employeeId") Long employeeId, @Param("name") String name);

    /**
     * 资料流转-新增 客户列表
     *
     * @param employeeId
     * @param name
     * @return
     */
    List<UniversalCustomerOrderVO> selectUniversalDataFlowCustomerOrder(@Param("employeeId") Long employeeId, @Param("name") String name);

    VehicleInformationVO selectVehicleInformation(Long orderId);

    ApplyLicensePlateDepositInfoVO selectApplyLicensePlateDepositInfo(Long orderId);

    UniversalLoanFinancialPlanTempHisVO selectUniversalLoanFinancialPlanTempHis(@Param("orderId") Long orderId, @Param("hisId") Long hisId);

    UniversalLoanRefundApplyVO selectUniversalLoanRefundApply(@Param("orderId") Long orderId, @Param("refundId") Long refundId);

    UniversalInfoVO selectUniversalInfo(Long orderId);

    UniversalApprovalInfo selectUniversalApprovalInfo(@Param("taskDefinitionKey") String taskDefinitionKey, @Param("orderId") Long orderId);

    UniversalLoanInfoVO selectUniversalLoanInfo(Long orderId);

    List<String> selectUniversalRelevanceOrderId(Long orderId);

    UniversalCarInfoVO selectUniversalCarInfo(Long orderId);

    List<UniversalRelationCustomerVO> selectUniversalRelationCustomer(Long orderId);

    List<String> selectUniversalRelevanceOrderIdByCustomerId(@Param("orderId") Long orderId, @Param("customerId") Long customerId);

    UniversalRemitDetails selectUniversalRemitDetails(Long orderId);

    UniversalCostDetailsVO selectUniversalCostDetails(Long orderId);

    List<UniversalCreditInfoVO> selectUniversalCreditInfo(Long orderId);

    UniversalHomeVisitInfoVO selectUniversalHomeVisitInfo(Long orderId);

    UniversalSupplementInfoVO selectUniversalSupplementInfo(Long orderId);

    List<UniversalCustomerVO> selectUniversalCustomer(Long orderId);

    UniversalCustomerDetailVO selectUniversalCustomerDetail(@Param("orderId") Long orderId, @Param("customerId") Long customerId);

    List<UniversalCustomerFileVO> selectUniversalCustomerFile(Long customerId);

    /**
     * 资料增补  文件列表（最新一次增补）
     *
     * @param orderId
     * @return
     */
    List<UniversalMaterialRecordVO> selectUniversalMaterialRecord(Long orderId);

    List<UniversalMaterialRecordVO> selectUniversalCustomerFileByTypes(@Param("orderId") Long orderId, @Param("types") Set<Byte> types);

    Long selectOrderIdbyPrincipalCustId(Long customerId);

    UniversalOverdueInfo selectUniversalOverdueInfo(Long orderId);

    List<UniversalLoanRepaymentPlan> selectUniversalLoanRepaymentPlan(Long orderId);

    List<UniversalMaterialRecordVO> selectUniversalCustomerFiles(@Param("customerId") Long customerId, @Param("types") Set<Byte> types);

    List<UniversalCollectionRecord> selectUniversalCollectionRecord(Long orderId);

    UniversalCollectionRecordDetail selectUniversalCollectionRecordDetail(Long collectionId);

    List<UniversalTelephoneCollectionEmployee> selectUniversalTelephoneCollectionEmployee();

    List<UniversalUndistributedCollection> selectUniversalUndistributedCollection();

    /**
     * 资料流转
     *
     * @param dataFlowId 资料流转单ID
     * @return
     */
    UniversalDataFlowDetailVO selectUniversalDataFlowDetail(Long dataFlowId);

    /**
     * 合同归档
     *
     * @param orderId
     * @return
     */
    UniversalMaterialManageVO selectUniversalMaterialManage(Long orderId);

    /**
     * 银行卡寄送单
     *
     * @param orderId
     * @return
     */
    UniversalBankCardSendVO selectUniversalBankCardSend(Long orderId);

    //=======================================================================

    List<InsuranceCustomerVO> selectInsuranceCustomer(Long orderId);

    InsuranceCustomerVO selectInsuranceCustomerNormalizeInsuranceYear(Long orderId);

    List<InsuranceRelevanceVO> selectInsuranceRelevance(Long insuranceInfoId);

    FinancialSchemeVO selectFinancialScheme(Long orderId);

    CostCalculateInfoVO selectCostCalculateInfo(Long orderId);

    List<GpsVO> selectGpsByOrderId(Long orderId);

    BankLendRecordVO selectBankLendRecordDetail(Long orderId);

    Long selectOrderIdByIDCard(String idCard);

    BankCardRecordVO selectBankCardRecordDetail(Long orderId);

    boolean checkCustomerHavingCreditON14Day(String idCard);
}
