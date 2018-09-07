package com.yunche.loan.mapper;


import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.SocialCreditChartVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChartDOMapper
{
    List<SocialCreditChartVO> selectSocialCreditChartVO(SocialCreditChartParam param);

    List selectBankCreditChartVO(BankCreditChartParam param);

    List selectFinancialDepartmentRemitDetailChartVO(FinancialDepartmentRemitDetailChartParam param);

    List selectMortgageOverdueChartVO(MortgageOverdueParam param);

    List selectMaterialReviewChartVO(MaterialReviewParam param);
}
