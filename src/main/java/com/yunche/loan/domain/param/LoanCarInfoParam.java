package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.LoanCarInfoVO;
import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/4
 */
@Data
public class LoanCarInfoParam extends LoanCarInfoVO {
    /**
     * 业务单ID
     */
    private String orderId;
}
