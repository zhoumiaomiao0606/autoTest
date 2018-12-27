package com.yunche.loan.mapper;


import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChartDOMapper
{
    List<SocialCreditChartVO> selectSocialCreditChartVO(SocialCreditChartParam param);

    List<BankCreditChartVO> selectBankCreditChartVO(BankCreditChartParam param);

    List selectFinancialDepartmentRemitDetailChartVO(FinancialDepartmentRemitDetailChartParam param);

    List selectMortgageOverdueChartVO(MortgageOverdueParam param);

    List selectMaterialReviewChartVO(MaterialReviewParam param);

    List selectAwaitRemitDetailChartVO(AwaitRemitDetailChartParam param);

    List selectCompanyRemitDetailChartVO(CompanyRemitDetailChartParam param);

    List<HzBankNotMortgageVO> hzBankNotMortgage(MaterialReviewParam param);

    List<BusDataFlowVO> busDataFlow(MaterialReviewParam param);

    List<PaperQuestionWarningVO> paperQuestionWarning(MaterialReviewParam param);

    List<ChannelPrescriptionVO> channelPrescription(MaterialReviewParam param);

    List<CreditPrescriptionVO> creditPrescription(MaterialReviewParam param);
}
