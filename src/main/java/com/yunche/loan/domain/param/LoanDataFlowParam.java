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
     * 合同编号                         -资料流转共享字段
     */
    private String contractNum;
    /**
     * 是否含抵押资料 (0-否;1-是;)       -资料流转共享字段
     */
    private Byte hasMortgageContract;
}
