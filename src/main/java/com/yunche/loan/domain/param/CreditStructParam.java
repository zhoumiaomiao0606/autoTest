package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.*;
import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/2/19
 */
@Data
public class CreditStructParam {

    private Long customerId;
    private String customerName;
    private Byte customerType;

    private CreditStructBlackAshSignDO creditStructBlackAshSign;

    private CreditStructQueryCountDO creditStructQueryCount;

    private CreditStructSumDO creditStructSum;

    private List<CreditStructTradeDetailDO> creditStructTradeDetail;

    private List<CreditStructTradeDetailLoanDO> creditStructTradeDetailLoan;

    private List<CreditStructGuaranteeLoanDetailDO> creditStructGuaranteeLoanDetail;

    private List<CreditStructGuaranteeCreditCardDetailDO> creditStructGuaranteeCreditCardDetail;
}
