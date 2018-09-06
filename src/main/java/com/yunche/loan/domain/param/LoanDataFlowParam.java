package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.LoanDataFlowDO;
import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/9/6
 */
@Data
public class LoanDataFlowParam extends LoanDataFlowDO {

    /**
     * 合同编号
     */
    private String contractNum;
}
