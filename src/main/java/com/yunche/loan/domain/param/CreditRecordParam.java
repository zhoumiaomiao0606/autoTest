package com.yunche.loan.domain.param;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/3
 */
@Data
public class CreditRecordParam {
    /**
     * 征信信息ID
     */
    private Long id;
    /**
     * 客户ID
     */
    private Long customerId;
    /**
     * 征信类型
     */
    private Byte type;
    /**
     * 结果
     */
    private Byte result;
    /**
     * 备注
     */
    private String info;
    /**
     * 征信审核结果
     */
    private Byte approveResult;
    /**
     * 征信审核结果备注
     */
    private String approveInfo;
    /**
     * 附加条件
     */
    private String addCondition;
}
