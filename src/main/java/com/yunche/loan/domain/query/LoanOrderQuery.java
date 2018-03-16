package com.yunche.loan.domain.query;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/2
 */
@Data
public class LoanOrderQuery extends BaseQuery {
    /**
     * 业务单号
     */
    private Long orderId;
    /**
     * 当前任务节点ID
     */
    private String taskDefinitionKey;
    /**
     * 客户名称
     */
    private String customerName;
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

    /**
     * 多节点查询类型：
     * 1-征信申请列表【未查询：   [提交征信申请单后  ,   贷款业务申请单)        】;
     * 2-征信申请列表【已查询：   [贷款业务申请单    ,   end]                 】;
     * 3-贷款申请列表【待审核：   [提交贷款申请单后  ,   电审通过)             】;
     * 4-贷款申请列表【已审核：   [电审通过后       ,    end]                】;
     * 5-客户查询列表【在贷客户： [提交征信申请单后  ,    未放款)              】;
     * 6-客户查询列表【已贷客户： [已放款           ,    end]                】;
     */
    private Integer multipartType;
}
