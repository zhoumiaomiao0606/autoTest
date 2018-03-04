package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.LoanFinancialPlanVO;
import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/4
 */
@Data
public class LoanFinancialPlanParam extends LoanFinancialPlanVO {
    /**
     * 业务单ID
     */
    private String orderId;
}
