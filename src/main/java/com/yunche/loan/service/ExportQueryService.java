package com.yunche.loan.service;

import com.yunche.loan.domain.param.*;

public interface ExportQueryService
{
    String exportBankCreditQuery(ExportBankCreditQueryVerifyParam exportBankCreditQueryVerifyParam);

    String expertSocialCreditQuery(ExportSocialCreditQueryVerifyParam exportSocialCreditQueryVerifyParam);

    String expertRemitDetailQuery(ExportRemitDetailQueryVerifyParam exportRemitDetailQueryVerifyParam);

    String expertMaterialReviewQuery(ExportMaterialReviewQueryVerifyParam exportMaterialReviewQueryVerifyParam);

    String expertMortgageOverdueQuery(ExportMortgageOverdueQueryVerifyParam exportMortgageOverdueQueryVerifyParam);

    String exportOrders(ExportOrdersParam exportOrdersParam);

    String expertRemitDetailQueryForRemitOrder(ExportRemitDetailQueryVerifyParam exportRemitDetailQueryVerifyParam);

    String exportCustomerInfo();
}
