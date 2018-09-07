package com.yunche.loan.mapper;


import com.yunche.loan.domain.param.BankCreditChartParam;
import com.yunche.loan.domain.param.FinancialDepartmentRemitDetailChartParam;
import com.yunche.loan.domain.param.MortgageOverdueParam;
import com.yunche.loan.domain.param.SocialCreditChartParam;
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
}
