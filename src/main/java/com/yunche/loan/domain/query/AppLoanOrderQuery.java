package com.yunche.loan.domain.query;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Data
public class AppLoanOrderQuery extends BaseQuery {
    /**
     * 当前任务节点ID
     */
    private String taskDefinitionKey;
    /**
     * 业务单号
     */
    private String orderId;
    /**
     * 客户名称
     */
    private String name;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 业务员ID
     */
    private Long salesmanId;
    /**
     * 当前任务节点审核状态:  0-全部;   1-已提交(审核);   2-未提交(审核);
     */
    private Integer taskStatus;
}
