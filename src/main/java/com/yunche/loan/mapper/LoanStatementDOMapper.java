package com.yunche.loan.mapper;

import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoanStatementDOMapper {


    List<TelephoneVerifyNodeOrdersVO> statisticsTelephoneVerifyNodeOrders(TelephoneVerifyParam telephoneVerifyParam);

    List<ExportBankCreditQueryVO> exportBankCreditQuerys(ExportBankCreditQueryVerifyParam expertBankCreditQueryVerifyParam);


    List<ExportSocialCreditQueryVO> exportSocialCreditQuerys(ExportSocialCreditQueryVerifyParam exportSocialCreditQueryVerifyParam);

    List<ExportRemitDetailQueryVO> exportRemitDetailQuerys(ExportRemitDetailQueryVerifyParam exportRemitDetailQueryVerifyParam);

    List<ExportMaterialReviewDetailQueryVO> exportMaterialReviewQuerys(ExportMaterialReviewQueryVerifyParam exportMaterialReviewQueryVerifyParam);

    List<ExportMortgageOverdueQueryVO> exportMortgageOverdueQuerys(ExportMortgageOverdueQueryVerifyParam exportMortgageOverdueQueryVerifyParam);
}
