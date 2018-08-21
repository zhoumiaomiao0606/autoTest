package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/2
 */
@Data
public class TaskStateVO {

    private String taskDefinitionKey;

    private String taskId;

    private String taskName;
    /**
     * 1-已提交;2-未提交;3-打回修改;   4/5/6-未提交([电审]等级);
     *
     * 11-已结单; 12-已弃单;
     *
     * 21-已退款([打款确认]节点);
     *
     * 22-操作锁定中(退款申请中/金融方案修改申请中);
     *
     * 具体参见：loan_process表
     */
    private Byte taskStatus;

    private String taskStatusText;
}
