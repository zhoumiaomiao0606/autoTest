package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.LoanHomeVisitVO;
import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/4
 */
@Data
public class LoanHomeVisitParam extends LoanHomeVisitVO {
    /**
     * 业务单ID
     */
    private String orderId;
}
