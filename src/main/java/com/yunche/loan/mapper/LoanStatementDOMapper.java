package com.yunche.loan.mapper;

import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.LoanCreditExportQuery;
import com.yunche.loan.domain.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanStatementDOMapper {


    List<TelephoneVerifyNodeOrdersVO> statisticsTelephoneVerifyNodeOrders(TelephoneVerifyParam telephoneVerifyParam);

    List<ExportBankCreditQueryVO> exportBankCreditQuerys(ExportBankCreditQueryVerifyParam expertBankCreditQueryVerifyParam);


    List<ExportSocialCreditQueryVO> exportSocialCreditQuerys1(ExportSocialCreditQueryVerifyParam exportSocialCreditQueryVerifyParam);

    List<ExportSocialCreditQueryVO> exportSocialCreditQuerys2(ExportSocialCreditQueryVerifyParam exportSocialCreditQueryVerifyParam);


    List<ExportRemitDetailQueryVO> exportRemitDetailQuerys(ExportRemitDetailQueryVerifyParam exportRemitDetailQueryVerifyParam);

    List<ExportMaterialReviewDetailQueryVO> exportMaterialReviewQuerys(ExportMaterialReviewQueryVerifyParam exportMaterialReviewQueryVerifyParam);

    List<ExportMortgageOverdueQueryVO> exportMortgageOverdueQuerys(ExportMortgageOverdueQueryVerifyParam exportMortgageOverdueQueryVerifyParam);

    List<ExportOrdersVO> exportOrders(ExportOrdersParam exportOrdersParam);

    List<ExportRemitDetailQueryForRemitOrderVO> exportRemitDetailForRemitOrderQuerys(ExportRemitDetailQueryVerifyParam exportRemitDetailQueryVerifyParam);

    List<ExportCustomerInfoVO> exportCustomerInfo(ExportCustomerInfoParam exportCustomerInfoParam);

    List<FamilyLinkManVO> exportFamilyLinkManList(Long pCustomerId);

    List<GuarantorLinkManVO> exportGuarantorLinkManList(Long pCustomerId);

    List<ExportApplyLoanPushVO> exportApplyLoanPush(ExportApplyLoanPushParam param);

    List<ExportApplyLoanPushVO> selectApplyLoanProcess(ExportApplyLoanPushParam param);

    List<ExportErrorOrderVO> exportErrorOrder(ExportApplyLoanPushParam param);

    List<JinTouHangRepayInfoVO> exportJinTouHangRepayInfo(ExportApplyLoanPushParam param);

    List<JinTouHangInterestRegisterVO> exportJinTouHangInterestRegister(ExportApplyLoanPushParam param);

    List<String> usertaskMaterialPrintUsers();

    List<CreditPicExportVO> selectCreditPicExport(LoanCreditExportQuery param);

    String selectMaxLevelByOrderId(@Param("orderId")Long orderId);

}
