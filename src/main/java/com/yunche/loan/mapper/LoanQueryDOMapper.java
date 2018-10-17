package com.yunche.loan.mapper;

import com.yunche.loan.domain.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface LoanQueryDOMapper {

    List<UniversalLegworkReimbursement> selectUniversalFileByLegworkReimbursementId(Long legworkReimbursementId);

    List<Long> selectEmpBizAreaPartnerIds(Long employeeId);

    boolean selectCheckOrderInBankInterfaceSerial(@Param("orderId") Long orderId,
                                                  @Param("transCode") String transCode);

    List<BankInterFaceSerialOrderStatusVO> selectBankInterFaceSerialOrderStatusByOrderId(@Param("orderId") Long orderId,
                                                                                         @Param("transCode") String transCode);

    List<UniversalBankInterfaceFileSerialDO> selectSuccessBankInterfaceFileSerialBySeriesNoAndFileType(@Param("serialNo") String serialNo,
                                                                                                       @Param("fileType") String fileType);

    /**
     * 视频面签-fileURL
     *
     * @param orderId
     * @return
     */
    String selectVideoFacePath(Long orderId);

    BankInterfaceSerialReturnVO selectLastBankInterfaceSerialByTransCode(@Param("customerId") Long customerId,
                                                                         @Param("transCode") String transCode);

    ApplyDiviGeneralInfoVO selectApplyDiviGeneralInfo(Long orderId);

    String selectLastBankInterfaceSerialStatusByTransCode(@Param("customerId") Long customerId,
                                                          @Param("transCode") String transCode);

    String selectLastBankInterfaceSerialNoteByTransCode(@Param("customerId") Long customerId,
                                                        @Param("transCode") String transCode);

    UniversalMaterialRecordVO getUniversalCustomerFilesByType(@Param("customerId") Long customerId,
                                                              @Param("type") Byte type);

    UniversalBankInterfaceSerialVO selectUniversalLatestBankInterfaceSerial(@Param("customerId") Long customerId,
                                                                            @Param("transCode") String transCode);

    String selectTelephoneVerifyLevel(@Param("loginUserId") Long loginUserId);

    boolean checkCollectionUserRole(@Param("loginUserId") Long loginUserId);

    List<UniversalCustomerOrderVO> selectUniversalModifyCustomerOrder(@Param("employeeId") Long employeeId,
                                                                      @Param("name") String name,
                                                                      @Param("maxGroupLevel") Long maxGroupLevel,
                                                                      @Param("juniorIds") Set<String> juniorIds);

    List<UniversalCustomerOrderVO> selectUniversalRefundCustomerOrder(@Param("employeeId") Long employeeId,
                                                                      @Param("name") String name,
                                                                      @Param("maxGroupLevel") Long maxGroupLevel,
                                                                      @Param("juniorIds") Set<String> juniorIds);

    List<UniversalCustomerOrderVO> selectUniversalLoanApplyCustomerOrder(@Param("employeeId") Long employeeId,
                                                                         @Param("name") String name,
                                                                         @Param("maxGroupLevel") Long maxGroupLevel,
                                                                         @Param("juniorIds") Set<String> juniorIds);

    /**
     * 资料流转-新增 客户列表
     *
     * @param employeeId
     * @param name
     * @return
     */
    List<UniversalCustomerOrderVO> selectUniversalDataFlowCustomerOrder(@Param("employeeId") Long employeeId,
                                                                        @Param("name") String name,
                                                                        @Param("maxGroupLevel") Long maxGroupLevel,
                                                                        @Param("juniorIds") Set<String> juniorIds);

    VehicleInformationVO selectVehicleInformation(Long orderId);

    ApplyLicensePlateDepositInfoVO selectApplyLicensePlateDepositInfo(Long orderId);

    UniversalLoanFinancialPlanTempHisVO selectUniversalLoanFinancialPlanTempHis(@Param("orderId") Long orderId,
                                                                                @Param("hisId") Long hisId);

    UniversalLoanRefundApplyVO selectUniversalLoanRefundApply(@Param("orderId") Long orderId,
                                                              @Param("refundId") Long refundId);

    /**
     * 订单基础公用信息
     *
     * @param orderId
     * @return
     */
    UniversalBaseInfoVO selectUniversalBaseInfo(Long orderId);

    /**
     * 订单详细公用信息
     *
     * @param orderId
     * @return
     */
    UniversalInfoVO selectUniversalInfo(Long orderId);

    UniversalApprovalInfo selectUniversalApprovalInfo(@Param("taskDefinitionKey") String taskDefinitionKey,
                                                      @Param("orderId") Long orderId);

    UniversalLoanInfoVO selectUniversalLoanInfo(Long orderId);

    List<String> selectUniversalRelevanceOrderId(Long orderId);

    UniversalCarInfoVO selectUniversalCarInfo(Long orderId);

    List<UniversalRelationCustomerVO> selectUniversalRelationCustomer(Long orderId);

    List<String> selectUniversalRelevanceOrderIdByCustomerId(@Param("orderId") Long orderId,
                                                             @Param("customerId") Long customerId);

    UniversalRemitDetails selectUniversalRemitDetails(Long orderId);

    UniversalRemitDetails selectAppUniversalRemitDetails(Long orderId);

    UniversalCostDetailsVO selectUniversalCostDetails(Long orderId);

    List<UniversalCreditInfoVO> selectUniversalCreditInfo(Long orderId);

    UniversalHomeVisitInfoVO selectUniversalHomeVisitInfo(Long orderId);

    List<UniversalCustomerVO> selectUniversalCustomer(Long orderId);

    UniversalCustomerDetailVO selectUniversalCustomerDetail(@Param("orderId") Long orderId,
                                                            @Param("customerId") Long customerId);

    /**
     * 当前客户的 文件列表  （包含：正常上传 & 增补上传，未根据upload_type作聚合）
     * <p>
     * tips：不能直接用此方法，请用LoanQueryService！！！
     * tips：不能直接用此方法，请用LoanQueryService！！！
     * tips：不能直接用此方法，请用LoanQueryService！！！
     * <p>
     * 请使用：
     *
     * @param customerId
     * @return
     * @see com.yunche.loan.service.LoanQueryService#selectUniversalCustomerFile
     * @see com.yunche.loan.service.LoanQueryService#selectUniversalCustomerFile
     * @see com.yunche.loan.service.LoanQueryService#selectUniversalCustomerFile
     */
    List<UniversalCustomerFileVO> selectUniversalCustomerFile(Long customerId);

    /**
     * 主贷人指定文件类型 的文件列表
     *
     * @param orderId
     * @param types
     * @return
     */
    List<UniversalMaterialRecordVO> selectUniversalCustomerFileByTypes(@Param("orderId") Long orderId,
                                                                       @Param("types") Set<Byte> types);

    Long selectOrderIdbyPrincipalCustId(Long customerId);

    UniversalOverdueInfo selectUniversalOverdueInfo(Long orderId);

    List<UniversalLoanRepaymentPlan> selectUniversalLoanRepaymentPlan(Long orderId);

    List<UniversalMaterialRecordVO> selectUniversalCustomerFiles(@Param("customerId") Long customerId,
                                                                 @Param("types") Set<Byte> types);

    /**
     * 催收
     *
     * @param orderId
     * @return
     */
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
     * 资料审核     -资料齐全日期
     *
     * @param orderId
     * @return
     */
    UniversalMaterialAuditVO selectUniversalMaterialAudit(Long orderId);

    /**
     * 银行卡寄送单
     *
     * @param orderId
     * @return
     */
    UniversalBankCardSendVO selectUniversalBankCardSend(Long orderId);

    /**
     * 资料增补单 详情
     * <p>
     * tips：不能直接使用当前方法！！！
     * tips：不能直接使用当前方法！！！
     * tips：不能直接使用当前方法！！！
     * <p>
     * 请使用：
     *
     * @param infoSupplementId 资料增补单ID
     * @return
     * @see com.yunche.loan.service.LoanQueryService#selectUniversalInfoSupplementDetail
     * @see com.yunche.loan.service.LoanQueryService#selectUniversalInfoSupplementDetail
     * @see com.yunche.loan.service.LoanQueryService#selectUniversalInfoSupplementDetail
     */
    List<UniversalInfoSupplementVO> selectUniversalInfoSupplement(Long infoSupplementId);

    /**
     * 单个订单 -的所有增补历史   -已提交的
     * <p>
     * tips：不能直接使用当前方法！！！
     * tips：不能直接使用当前方法！！！
     * tips：不能直接使用当前方法！！！
     * <p>
     * 请使用：
     *
     * @param orderId 订单ID
     * @return
     * @see com.yunche.loan.service.LoanQueryService#selectUniversalInfoSupplementHistory
     * @see com.yunche.loan.service.LoanQueryService#selectUniversalInfoSupplementHistory
     * @see com.yunche.loan.service.LoanQueryService#selectUniversalInfoSupplementHistory
     */
    List<UniversalInfoSupplementVO> selectUniversalCollectionInfoSupplement(Long orderId);

    /**
     * 资料增补  文件列表（最新一次增补）
     * <p>
     * 已废弃！！！
     * 已废弃！！！
     * 已废弃！！！
     *
     * @param orderId
     * @return
     */
    @Deprecated
    List<UniversalMaterialRecordVO> selectUniversalMaterialRecord(Long orderId);

    /**
     * 已废弃！！！
     * 已废弃！！！
     * 已废弃！！！
     *
     * @param orderId
     * @return
     */
    @Deprecated
    UniversalSupplementInfoVO selectUniversalSupplementInfo(Long orderId);


    //=======================================================================

    List<InsuranceCustomerVO> selectInsuranceCustomer(Long orderId);

    List<InsuranceCustomerVO> selectInsuranceCustomerByYear(@Param("orderId") Long orderId,
                                                            @Param("insuranceYear") Byte insuranceYear);

    InsuranceCustomerVO selectInsuranceCustomerNormalizeInsuranceYear(Long orderId);

    List<InsuranceRelevanceVO> selectInsuranceRelevance(Long insuranceInfoId);

    FinancialSchemeVO selectFinancialScheme(Long orderId);

    CostCalculateInfoVO selectCostCalculateInfo(Long orderId);

    List<GpsVO> selectGpsByOrderId(Long orderId);

    GpsDetailVO selectGpsDetailByOrderId(Long orderId);

    BankLendRecordVO selectBankLendRecordDetail(Long orderId);

    Long selectOrderIdByIDCard(String idCard);

    BankCardRecordVO selectBankCardRecordDetail(Long orderId);

    boolean checkCustomerHavingCreditON14Day(String idCard);

    List<UniversalTelephoneCollectionEmployee> selectUniversalInsuranceUrgeEmployee();

    BaseCustomerInfoVO selectBaseCustomerInfoInfo(Long orderId);

    VehicleInfoVO selectVehicleInfo(Long orderId);

    /**
     * @Author: ZhongMingxiao
     * @Param:
     * @return:
     * @Date:
     * @Description: 根据订单id查询抵押情况
     */
    MortgageInfoVO selectMortgageInfo(Long orderId);

    List<UniversalTelephoneCollectionEmployee> selectVisitDoorEmployee();

    /**
     * @Author: ZhongMingxiao
     * @Param:
     * @return:
     * @Date:
     * @Description: 模糊查询
     */
    List<CustomerOrderVO> selectCustomerOrder(@Param("name") String name);


    List<Long> selectOrderIdByIdCard(@Param("idCard")String idCard);
}
