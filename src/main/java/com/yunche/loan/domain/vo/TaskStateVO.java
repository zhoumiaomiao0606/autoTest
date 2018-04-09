package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/2
 */
@Data
public class TaskStateVO {

    private String taskDefinitionKey;

    private String taskName;
    /**
     * 1-已提交;2-未提交;3-打回修改;
     * <p>
     * 具体参见：loan_process表
     */
    private Byte taskStatus;

    private String taskStatusText;
}
